import io
import sys

sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8')
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

import pandas as pd
import networkx as nx
import matplotlib.pyplot as plt


# model_path = "../../../model/"
# data_path = "../../../data/"
model_path = sys.argv[1]
data_path = sys.argv[2]
print("开始加载模块信息...", flush=True)
module_info = pd.read_excel(data_path + "模块信息.xlsx")
print("模块信息已加载。", flush=True)
# print(module_info)

module_gene = module_info[['Symbol','WGCNA\nModule|color|kME\nSort Vector']]
print("筛选出 'Symbol' 和 'WGCNA\nModule|color|kME\nSort Vector' 列。", flush=True)
# print(module_gene)

module_gene.dropna(inplace=True)
print("已移除缺失值。", flush=True)
# print(module_gene)

module_gene[['Module']] = module_gene['WGCNA\nModule|color|kME\nSort Vector'].str.split('|', expand=True)[[0]]
print("模块信息拆分完成。", flush=True)
# print(module_gene)

module_gene.drop(['WGCNA\nModule|color|kME\nSort Vector'], axis=1, inplace=True)
print("已删除不必要的列。", flush=True)
# print(module_gene)

module_gene = module_gene[module_gene['Module'].str.startswith('M')]
print("仅保留以 'M' 开头的模块。", flush=True)
# print(module_gene)

module_gene.drop_duplicates(subset=['Symbol'], inplace=True)
print("已移除重复的基因符号。", flush=True)
# print(module_gene)

M1 = module_gene[module_gene['Module'].str.startswith('M1 ')]['Symbol'].tolist()
M2 = module_gene[module_gene['Module'].str.startswith('M2 ')]['Symbol'].tolist()
M3 = module_gene[module_gene['Module'].str.startswith('M3 ')]['Symbol'].tolist()
M4 = module_gene[module_gene['Module'].str.startswith('M4 ')]['Symbol'].tolist()
M5 = module_gene[module_gene['Module'].str.startswith('M5 ')]['Symbol'].tolist()
M6 = module_gene[module_gene['Module'].str.startswith('M6 ')]['Symbol'].tolist()
M7 = module_gene[module_gene['Module'].str.startswith('M7 ')]['Symbol'].tolist()
M8 = module_gene[module_gene['Module'].str.startswith('M8 ')]['Symbol'].tolist()
M9 = module_gene[module_gene['Module'].str.startswith('M9 ')]['Symbol'].tolist()
M10 = module_gene[module_gene['Module'].str.startswith('M10 ')]['Symbol'].tolist()
M11 = module_gene[module_gene['Module'].str.startswith('M11 ')]['Symbol'].tolist()
M12 = module_gene[module_gene['Module'].str.startswith('M12 ')]['Symbol'].tolist()
M13 = module_gene[module_gene['Module'].str.startswith('M13 ')]['Symbol'].tolist()
M14 = module_gene[module_gene['Module'].str.startswith('M14 ')]['Symbol'].tolist()
M15 = module_gene[module_gene['Module'].str.startswith('M15 ')]['Symbol'].tolist()
M16 = module_gene[module_gene['Module'].str.startswith('M16 ')]['Symbol'].tolist()
M17 = module_gene[module_gene['Module'].str.startswith('M17 ')]['Symbol'].tolist()
M18 = module_gene[module_gene['Module'].str.startswith('M18 ')]['Symbol'].tolist()
M19 = module_gene[module_gene['Module'].str.startswith('M19 ')]['Symbol'].tolist()
M20 = module_gene[module_gene['Module'].str.startswith('M20 ')]['Symbol'].tolist()
M21 = module_gene[module_gene['Module'].str.startswith('M21 ')]['Symbol'].tolist()
M22 = module_gene[module_gene['Module'].str.startswith('M22 ')]['Symbol'].tolist()
M23 = module_gene[module_gene['Module'].str.startswith('M23 ')]['Symbol'].tolist()
M24 = module_gene[module_gene['Module'].str.startswith('M24 ')]['Symbol'].tolist()
M25 = module_gene[module_gene['Module'].str.startswith('M25 ')]['Symbol'].tolist()
M26 = module_gene[module_gene['Module'].str.startswith('M26 ')]['Symbol'].tolist()
M27 = module_gene[module_gene['Module'].str.startswith('M27 ')]['Symbol'].tolist()
M28 = module_gene[module_gene['Module'].str.startswith('M28 ')]['Symbol'].tolist()
M29 = module_gene[module_gene['Module'].str.startswith('M29 ')]['Symbol'].tolist()
M30 = module_gene[module_gene['Module'].str.startswith('M30 ')]['Symbol'].tolist()
M31 = module_gene[module_gene['Module'].str.startswith('M31 ')]['Symbol'].tolist()
M32 = module_gene[module_gene['Module'].str.startswith('M32 ')]['Symbol'].tolist()
M33 = module_gene[module_gene['Module'].str.startswith('M33 ')]['Symbol'].tolist()
M34 = module_gene[module_gene['Module'].str.startswith('M34 ')]['Symbol'].tolist()
M35 = module_gene[module_gene['Module'].str.startswith('M35 ')]['Symbol'].tolist()
M36 = module_gene[module_gene['Module'].str.startswith('M36 ')]['Symbol'].tolist()
M37 = module_gene[module_gene['Module'].str.startswith('M37 ')]['Symbol'].tolist()
M38 = module_gene[module_gene['Module'].str.startswith('M38 ')]['Symbol'].tolist()
M39 = module_gene[module_gene['Module'].str.startswith('M39 ')]['Symbol'].tolist()
M40 = module_gene[module_gene['Module'].str.startswith('M40 ')]['Symbol'].tolist()
M41 = module_gene[module_gene['Module'].str.startswith('M41 ')]['Symbol'].tolist()
M42 = module_gene[module_gene['Module'].str.startswith('M42 ')]['Symbol'].tolist()
M43 = module_gene[module_gene['Module'].str.startswith('M43 ')]['Symbol'].tolist()
M44 = module_gene[module_gene['Module'].str.startswith('M44 ')]['Symbol'].tolist()

M = []
M.append(M1)
M.append(M2)
M.append(M3)
M.append(M4)
M.append(M5)
M.append(M6)
M.append(M7)
M.append(M8)
M.append(M9)
M.append(M10)
M.append(M11)
M.append(M12)
M.append(M13)
M.append(M14)
M.append(M15)
M.append(M16)
M.append(M17)
M.append(M18)
M.append(M19)
M.append(M20)
M.append(M21)
M.append(M22)
M.append(M23)
M.append(M24)
M.append(M25)
M.append(M26)
M.append(M27)
M.append(M28)
M.append(M29)
M.append(M30)
M.append(M31)
M.append(M32)
M.append(M33)
M.append(M34)
M.append(M35)
M.append(M36)
M.append(M37)
M.append(M38)
M.append(M39)
M.append(M40)
M.append(M41)
M.append(M42)
M.append(M43)
M.append(M44)
# print(M)

# print(len(M1),len(M2),len(M3),len(M4),len(M5),len(M6),len(M7),len(M8),len(M9),len(M10),len(M11),len(M12),len(M13),len(M14),len(M15),len(M16),len(M17),len(M18),len(M19),len(M20),len(M21),len(M22),len(M23),len(M24),len(M25),len(M26),len(M27),len(M28),len(M29),len(M30),len(M31),len(M32),len(M33),len(M34),len(M35),len(M36),len(M37),len(M38),len(M39),len(M40),len(M41),len(M42),len(M43),len(M44))

color = ['#F32727','#F6391B','#F64A09', '#FD7110', '#F59C33', '#FEB10A', '#F5D028', '#F2E70D', '#E3F90D', '#C5FD06', '#A1F808',
         '#88FB14', '#7DFD34', '#4BF519', '#46FD34', '#30F539', '#33FE58', '#11F55A', '#0FFA79', '#26F9A2', '#28FAC0', '#1EF5D8',
         '#37F8F8', '#19D5F2', '#20BFFB', '#23A1F8', '#2888FC', '#0B58FE', '#2C51F8', '#0713FC', '#3725F4', '#4816F3', '#6C18FE',
         '#942FFA', '#A613FA', '#C920FA', '#EA23FE', '#F521EB', '#FD33D8', '#F731B8', '#F52998', '#F63584', '#FA1F5B', '#FE2F4B']

l_dic={}
for i in range(len(M)):
    for j in range(len(M[i])):
        l_dic[M[i][j]]='M'+str(i+1)
# print(l_dic)
print("基因与模块的映射字典已创建。", flush=True)
# print(len(l_dic))
print(f"模块数量: {len(l_dic)}个基因被分配到 {len(M)} 个模块。", flush=True)

## 原代码（使用共表达网络）
## edge保存的是边信息，node保存的是节点信息
edge = pd.read_csv(data_path + "braak_3.csv.edges.txt", sep='\t')
node = pd.read_csv(data_path + "braak_3.csv.nodes.txt", sep='\t')
print("边信息和节点信息已加载。", flush=True)

## 如果使用基因调控网络，则需要根据tsv文件读取结果
## tsv文件的一行有三个数据，表示一条边，TF是边的调控节点，target是边的目标节点
b_3 = pd.read_csv(data_path + "adj_braak3.tsv",sep='\t')
print("基因调控网络数据已读取。", flush=True)

node_list = []
for i in range(44):
    l=[]
    for j in node['nodeName'].tolist():
        if j in M[i]:
            l.append(j)
    node_list.append(l)
    print(f"M{i + 1} 模块包含的节点数量: {len(l)}", flush=True)

ed = edge.values
ed = ed[:,:3]
# print(len(ed))
print(f"总边数: {len(ed)}", flush=True)

ed_n = []
for i in range(len(ed)):
    if ed[i][2]>=0.67:
        ed_n.append(ed[i])
# print(len(ed_n))
print(f"边权重大于0.67的边数量: {len(ed_n)}", flush=True)

edge_node = []
for i in range(44):
    s = []
    edge_node.append(s)

for j in range(len(ed_n)):
    for i in range(44):
        if (ed_n[j][0] in node_list[i]) and (ed_n[j][1] in node_list[i]):
            edge_node[i].append(ed_n[j].tolist())

print("开始创建网络图...", flush=True)
G = nx.Graph()
for i in range(44):
    # G.add_edges_from([(e[0], e[1], {'weight': e[2]}) for e in edge_node[i]])
    for e in edge_node[i]:  # 遍历每个模块的边
        G.add_edge(e[0], e[1], weight=e[2])  # 添加边
        print(f"添加边: {e[0]} -> {e[1]}, 权重: {e[2]:.2f}", flush=True)  # 输出边的信息

print("正在添加节点...", flush=True)
for i in range(44):
    G.add_nodes_from(node_list[i])

widths = nx.get_edge_attributes(G, 'weight')

for key in widths:
    widths[key] = 10 * widths[key]

print("正在布局节点...", flush=True)
nodelist = G.nodes()

pos = nx.spring_layout(G, k=0.15)
# pos = nx.spiral_layout(G)
# pos = nx.circular_layout(G)
# pos = nx.spectral_layout(G)
# pos = nx.kamada_kawai_layout(G)

nodes = {}
for i in range(44):
    nodes[color[i]] = node_list[i]

plt.figure(figsize=(20, 20), dpi=250)

print("正在绘制节点...")
for node_color, nodelist in nodes.items():
    nx.draw_networkx_nodes(G, pos,
                           nodelist=nodelist,
                           node_size=10,
                           node_color=node_color,
                           alpha=0.9)

print("正在绘制边...", flush=True)
nx.draw_networkx_edges(G, pos,
                       edgelist=widths.keys(),
                       width=0.1,
                       edge_color='#808080',
                       alpha=0.7)

print("正在保存图像...", flush=True)
plt.savefig(data_path + "GRN.png", format="PNG", dpi=250, bbox_inches='tight', pad_inches=0)
print("图像已保存", flush=True)

# 输出节点、边和模块的数量
print("网络图节点数量:", G.number_of_nodes(), flush=True)
print("网络图边数量:", G.number_of_edges(), flush=True)
print("模块数量:", len(M), flush=True)

plt.show()
