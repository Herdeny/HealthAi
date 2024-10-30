# 基因筛选

import io
import sys

sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8')
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

import numpy as np
import pandas as pd

# model_path = "../../../model"
# data_path = "../../../data"
model_path = sys.argv[1]
data_path = sys.argv[2]
mapping = pd.read_csv(model_path + "label_mapping.csv")

mapping_filtered = mapping[~mapping['Gene_name'].isin(['1-Sep', '10-Sep', '11-Sep',
'14-Sep', '15-Sep', '2-Mar', '2-Sep', '3-Sep', '4-Sep', '5-Sep', '6-Sep', '7-Sep',
'8-Sep', '9-Sep', 'NRD1', 'SRPR'])]


df = pd.read_csv(data_path + "GSM4432654_EC10.csv",header = 0,index_col=0) ##用户输入数据
# 行列转置
df=df.T
df = df.loc[mapping_filtered["Gene_name"].tolist()]

# 时空融合
sp = pd.read_csv(model_path + "final_spatial_fusion.csv", index_col=None, header=0)  ##这是ROSMAP数据空间融合后的结果
sp.index = mapping['Gene_name']
sp_filtered = sp[~sp.index.isin(['1-Sep', '10-Sep', '11-Sep', '14-Sep', '15-Sep',
                                 '2-Mar', '2-Sep', '3-Sep', '4-Sep', '5-Sep', '6-Sep', '7-Sep', '8-Sep', '9-Sep',
                                 'NRD1', 'SRPR'])]
graph_embedding = sp_filtered.values
patient_matrix = df.values
import scipy as sp
from functools import reduce
import scipy.spatial.distance as sd
from sklearn.metrics.pairwise import euclidean_distances

def laplacian(W, normed=False, return_diag=False):
    n_nodes = W.shape[0]
    lap = -np.asarray(W)
    lap.flat[::n_nodes + 1] = 0
    d = -lap.sum(axis=0)
    if normed:
        d = np.sqrt(d)
        d_zeros = (d == 0)
        d[d_zeros] = 1
        lap /= d
        lap /= d[:, np.newaxis]
        lap.flat[::n_nodes + 1] = 1 - d_zeros
    else:
        lap.flat[::n_nodes + 1] = d
    if return_diag:
        return lap, d
    return lap

def _manifold_setup(Wx, Wy, Wxy, mu):
    Wxy = mu * (Wx.sum() + Wy.sum()) / (2 * Wxy.sum()) * Wxy
    W = np.asarray(np.bmat(((Wx, Wxy), (Wxy.T, Wy))))
    return laplacian(W)

def _manifold_decompose(L, d1, d2, num_dims, eps, vec_func=None):
    vals, vecs = np.linalg.eig(L)
    idx = np.argsort(vals)
    for i in range(len(idx)):
        if vals[idx[i]] >= eps:
            break
    vecs = vecs.real[:, idx[i:]]
    if vec_func:
        vecs = vec_func(vecs)
    for i in range(vecs.shape[1]):
        vecs[:, i] /= np.linalg.norm(vecs[:, i])
    map1 = vecs[:d1, :num_dims]
    map2 = vecs[d1:d1 + d2, :num_dims]
    return map1, map2

def _linear_decompose(X, Y, L, num_dims, eps):
    Z = sp.linalg.block_diag(X.T, Y.T)
    u, s, _ = np.linalg.svd(np.dot(Z, Z.T))
    Fplus = np.linalg.pinv(np.dot(u, np.diag(np.sqrt(s))))
    T = reduce(np.dot, (Fplus, Z, L, Z.T, Fplus.T))
    L = 0.5 * (T + T.T)
    d1, d2 = X.shape[1], Y.shape[1]
    return _manifold_decompose(L, d1, d2, num_dims, eps, lambda v: np.dot(Fplus.T, v))

class LinearAlignment(object):
    def project(self, X, Y, num_dims=None):
        if num_dims is None:
            return np.dot(X, self.pX), np.dot(Y, self.pY)
        return np.dot(X, self.pX[:, :num_dims]), np.dot(Y, self.pY[:, :num_dims])

    def apply_transform(self, other):
        self.pX = np.dot(self.pX, other.pX)
        self.pY = np.dot(self.pY, other.pY)

class ManifoldLinear(LinearAlignment):
    def __init__(self, X, Y, corr, num_dims, Wx, Wy, mu=0.9, eps=1e-8):
        super(ManifoldLinear, self).__init__()
        L = _manifold_setup(Wx, Wy, corr.matrix(), mu)
        self.pX, self.pY = _linear_decompose(X, Y, L, num_dims, eps)

class Correspondence(object):
    def __init__(self, pairs=None, matrix=None):
        assert pairs is not None or matrix is not None, \
            'Must provide either pairwise or matrix correspondences'
        self._pairs = pairs
        self._matrix = matrix

    def pairs(self):
        if self._pairs is None:
            self._pairs = np.vstack(np.nonzero(self._matrix)).T
        return self._pairs

    def matrix(self):
        if self._matrix is None:
            self._matrix = np.zeros(self._pairs.max(axis=0) + 1)
            for i in self._pairs:
                self._matrix[i[0], i[1]] = 1
        return self._matrix

    def dist_from(self, other):
        '''Calculates the warping path distance from this correspondence to another.
           Based on the implementation from CTW.'''
        B1, B2 = self._bound_row(), other._bound_row()
        gap0 = np.abs(B1[:-1, 1] - B2[:-1, 1])
        gap1 = np.abs(B1[1:, 0] - B2[1:, 0])
        d = gap0.sum() + (gap0 != gap1).sum() / 2.0
        return d / float(self.pairs()[-1, 0] * other.pairs()[-1, 0])

    def warp(self, A, XtoY=True):
        '''Warps points in A by pairwise correspondence'''
        P = self.pairs()
        if not XtoY:
            P = np.fliplr(P)
        warp_inds = np.zeros(A.shape[0], dtype=np.int)
        j = 0
        for i in range(A.shape[0]):
            while P[j, 0] < i:
                j += 1
            warp_inds[i] = P[j, 1]
        return A[warp_inds]

    def _bound_row(self):
        P = self.pairs()
        n = P.shape[0]
        B = np.zeros((P[-1, 0] + 1, 2), dtype=np.int)
        head = 0
        while head < n:
            i = P[head, 0]
            tail = head + 1
            while tail < n and P[tail, 0] == i:
                tail += 1
            B[i, :] = P[(head, tail - 1), 1]
            head = tail
        return B


izip = zip
class Metric(object):
    def __init__(self, dist, name):
        self.dist = dist  # dist(x,y): distance between two points
        self.name = name

    def within(self, A):
        '''pairwise distances between each pair of rows in A'''
        return sd.squareform(sd.pdist(A, self.name), force='tomatrix')

    def between(self, A, B):
        '''cartesian product distances between pairs of rows in A and B'''
        return sd.cdist(A, B, self.name)

    def pairwise(self, A, B):
        '''distances between pairs of rows in A and B'''
        return np.array([self.dist(a, b) for a, b in izip(A, B)])


class SparseL2Metric(Metric):
    '''scipy.spatial.distance functions don't support sparse inputs,
    so we have a separate SparseL2 metric for dealing with them'''

    def __init__(self):
        Metric.__init__(self, euclidean_distances, 'sparseL2')

    def within(self, A):
        return euclidean_distances(A, A)

    def between(self, A, B):
        return euclidean_distances(A, B)

    def pairwise(self, A, B):
        '''distances between pairs of rows in A and B'''
        return Metric.pairwise(self, A, B).flatten()


SquaredL2 = Metric(sd.sqeuclidean, 'sqeuclidean')


def neighbor_graph(X, metric=SquaredL2, k=None, epsilon=None, symmetrize=True):
    '''Construct an adj matrix from a matrix of points (one per row)'''
    assert (k is None) ^ (epsilon is None), "Must provide `k` xor `epsilon`"
    dist = metric.within(X)
    adj = np.zeros(dist.shape)  # TODO: scipy.sparse support, or at least use a smaller dtype
    if k is not None:
        # do k-nearest neighbors
        nn = np.argsort(dist)[:, :min(k + 1, len(X))]
        # nn's first column is the point idx, rest are neighbor idxs
        if symmetrize:
            for inds in nn:
                adj[inds[0], inds[1:]] = 1
                adj[inds[1:], inds[0]] = 1
        else:
            for inds in nn:
                adj[inds[0], inds[1:]] = 1
    else:
        # do epsilon-ball
        p_idxs, n_idxs = np.nonzero(dist <= epsilon)
        for p_idx, n_idx in zip(p_idxs, n_idxs):
            if p_idx != n_idx:  # ignore self-neighbor connections
                adj[p_idx, n_idx] = 1
        # epsilon-ball is typically symmetric, assuming a normal distance metric
    return adj

graph_embedding = sp_filtered.values
Wx = neighbor_graph(graph_embedding, k=5)
corr = Correspondence(matrix=np.eye(graph_embedding.shape[0]))
from sklearn.decomposition import PCA
from sklearn.preprocessing import StandardScaler
scaler = StandardScaler()
pca = PCA(n_components=100)
patient_matrix1 = []
patient_matrix2 = []

directory = data_path + "test_data/"

# 遍历目录下的文件
for filename in os.listdir(directory):
    # 确保只处理文件（可选）
    if os.path.isfile(os.path.join(directory, filename)):
        ##如果有多个矩阵，这一块代码（到patient_matrix2.append为止）需要进行for循环读取数据，每一个样本都需要进行融合
        df = pd.read_csv(directory + filename, header=0, index_col=0)
        a = pca.fit_transform(df.values.T)
        Wy = neighbor_graph(a, k=5)
        pX, pY = ManifoldLinear(graph_embedding, a, corr, 100, Wx,
                                Wy).project(graph_embedding, a)
        patient_matrix1.append(scaler.fit_transform(pX))
        patient_matrix2.append(scaler.fit_transform(pY))
patient_matrix1 = np.array(patient_matrix1)
patient_matrix2 = np.array(patient_matrix2)

# 预测结果，将时空融合中的patient_matrix2作为输入，进行预测。
X = patient_matrix2.reshape((patient_matrix2.shape[0], patient_matrix2.shape[1],
patient_matrix2.shape[2], 1))
import tensorflow as tf
##读入三个文件中的一个，想要预测CERAD就读入CERAD模型，想要预测Braak就读入Braak模型，想要预测Cogdx就读入Cogdx模型
best_model = tf.keras.models.load_model(model_path + 'best_model_tempro-spatialfusion_Cogdx.h5')
with tf.device("/cpu:0"):
    predictions = best_model.predict(X)

##predicted_labels就是我们需要的预测结果，也就是前端需要输出的结果（这是一个数字，预测出来的就是病人的疾病阶段）
predicted_labels = np.argmax(predictions, axis=1)


print(predicted_labels)

##当用户提供标签并且有多个样本的时候可以计算以下指标，主要用于评估模型的好坏，在这里自己可以进行判断，不需要放到前端
# # 计算准确率
# accuracy = accuracy_score(label, predicted_labels)
# print(f'Test Accuracy: {accuracy}')
# # 计算精确度
# precision = precision_score(label, predicted_labels, average='weighted')
# print(f'Precision: {precision}')
# # 计算召回率
# recall = recall_score(label, predicted_labels, average='macro')
# print(f'Recall: {recall}')
# # 计算F1分数
# f1 = f1_score(label, predicted_labels, average='weighted')
# print(f'F1 Score: {f1}')

# --------------------------------------------------------------------------------------------------

# predicted_labels = np.array([pred for pred in predicted_labels.tolist()])
# df = pd.DataFrame({'pred': predicted_labels})
# df.to_csv('./Braak.csv',index=None,columns=None)

df = pd.DataFrame(predictions)
df.to_csv(data_path + "Braak.csv", index=None, columns=None)

from sklearn.preprocessing import label_binarize
from sklearn.metrics import roc_curve
import matplotlib.pyplot as plt
from sklearn.metrics import auc

# methods = ["Braak","CERAD","CogDx"]
methods = ["Braak"]
colors = ["#CBE99A","#FDB96A","#87CFA4"]
plt.figure(figsize=(20, 20))
plt.rc('font', size=20)
 ## Braak、CERAD、CogDx三个变量分别读取预测的三个指标的predictions数据
Braak = pd.read_csv(data_path + "Braak.csv")
# CERAD = pd.read_csv("./CERAD.csv")
# CogDx = pd.read_csv("./CogDx.csv")
 ## 读取真实标签
true = pd.read_csv(model_path + "label.csv",header = 0,index_col=None)

true_label1 = list(map(int,true["Braak"].tolist()))
# true_label2 = list(map(int,true["CERAD"].tolist()))
# true_label3 = list(map(int,true["CogDx"].tolist()))
method1_labels = Braak.values[:,:]
# method2_labels = CERAD.values[:,:-1]
# method3_labels = CogDx.values[:,:-1]
true_labels_bin1 = label_binarize(true["Braak"].tolist(), classes = [0,1,2,3,4,5,6])
# true_labels_bin2 = label_binarize(true["CERAD"].tolist(),classes=
# [0]+list(np.unique(true_label2)))
# true_labels_bin3 = label_binarize(true["Cogimport matplotlib.pyplot as pltDx"].tolist(), classes=
# [0]+list(np.unique(true_label3)))
# methods_labels = [method1_labels, method2_labels, method3_labels]

# true_labels = [true_labels_bin1, true_labels_bin2, true_labels_bin3]

# for j, method_label in enumerate(methods_labels):
#     fpr1, tpr1, _ = roc_curve(true_labels[j].ravel(), method_label.ravel())
#     roc_auc = auc(fpr1, tpr1)
#     plt.plot(fpr1, tpr1, label='%s (AUC = %0.2f)' % (methods[j], roc_auc), lw=2,color=colors[j])
print(true_labels_bin1)
print(method1_labels)
fpr1, tpr1, _ = roc_curve(true_labels_bin1.ravel(), method1_labels.ravel())
roc_auc = auc(fpr1, tpr1)
plt.plot(fpr1, tpr1, label='%s (AUC = %0.2f)' % (methods[0], roc_auc), lw=2, color=colors[0])

plt.plot([0, 1], [0, 1], color='gray', lw=2, linestyle='--')
plt.xlim([0.0, 1.0])
plt.ylim([0.0, 1.05])
plt.xlabel('False Positive Rate')
plt.ylabel('True Positive Rate')
plt.legend(loc="lower right")
plt.tight_layout()
plt.savefig(data_path + 'ROC.png',dpi=750,bbox_inches='tight')
plt.show()

