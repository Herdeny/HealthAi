import io
import sys

sys.stderr = io.TextIOWrapper(sys.stderr.buffer, encoding='utf-8')
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')


import pandas as pd
import matplotlib.pyplot as plt
import networkx as nx

data_path = sys.argv[1]
file_path = sys.argv[2]
# file_path = "../../../data/adj.tsv"
grn_data = pd.read_csv(file_path, sep='\t')
grn_data = grn_data.iloc[:250, :3]

nodecolor = ['#F63B42', '#FB0909', '#F82C24', '#F94536', '#FE4026', '#FD3914', '#F33E11', '#F34813', '#F54C0B',
             '#F85509', '#FD5C02', '#FA7D2A', '#F47A1C',
             '#F98D2B', '#FD8608', '#F7A237', '#FA9D13', '#FAAD27', '#F9BA36', '#F8C137', '#FBC520', '#FAC908',
             '#FBD61C', '#F8D907', '#FBE926', '#FDF20B',
             '#FDFB32', '#EFF62B', '#EBF931', '#DCF41B', '#D6F430', '#CEFE05', '#C6F820', '#C5FE2D', '#AEF512',
             '#AEF925', '#9EF812', '#A5FE31', '#8BF811',
             '#90FD27', '#8EF838', '#79F323', '#79F72F', '#70F92C', '#69FA2D', '#55F61E', '#47F319', '#45F421',
             '#35F518', '#1FF809', '#1EF412', '#1DFE1A',
             '#1DFD23', '#2CF43A', '#2AFD41', '#20F73F', '#23F349', '#11F545', '#35F468', '#1FFE63', '#13F962',
             '#19F86F', '#19FE7A', '#0EFE7D', '#19FC8B',
             '#2AFE9D', '#37F6A6', '#38F7AF', '#1EF6AD', '#29FDBE', '#29F7C2', '#2AFACD', '#2BF8D3', '#16FDDD',
             '#15F4DE', '#1FFDEF', '#23F7F2', '#37F5F9',
             '#1BEBF8', '#33E4F8', '#0EDBFC', '#2FD9FE', '#05C5FB', '#12BCF7', '#32C1FE', '#27B4FC', '#2FAFFD',
             '#0B97FB', '#1993F9', '#288FF4', '#2A88F6',
             '#0A71FD', '#0E67F6', '#1C68F8', '#2061F7', '#2059F6', '#2856F5', '#3156F9', '#2444FE', '#253CFE',
             '#0415FD', '#3339FA', '#3835F7', '#4035F9',
             '#2A15FB', '#381AFB', '#5837F9', '#4212F3', '#6534F7', '#5A19F9', '#7839F7', '#7829FC', '#771DF9',
             '#892BFD', '#820BFE', '#9425F6', '#A028FA',
             '#A323F5', '#AA20F4', '#B21FF4', '#B91DF4', '#CD35FB', '#CB06FA', '#D817FB', '#E222FA', '#E419F4',
             '#F12EF8', '#FB07F9', '#FD23F3', '#FE0AE9',
             '#F52DDB', '#F628D4', '#F613C8', '#FA11C1', '#F431BD', '#FD29B8', '#FD14A8', '#FB26A5', '#F4309D',
             '#FD2695', '#F93793', '#F31B7A', '#F92B7C',
             '#F81063', '#F61C62', '#FD3069', '#FD164D', '#F81E4A', '#F43553', '#FE253E', '#FB0B1E']

a = set(grn_data['TF'])
print(len(a))
a = list(a)
a = a
b = set(grn_data['target'])
b = list(b)
c = set(b) - set(a)
c = list(c)

##设置节点颜色
lis = []
for i in range(len(a)):
    lis.append((a[i], nodecolor[i]))
for i in range(len(c)):
    lis.append((c[i], nodecolor[150]))

##设置节点大小
lis2 = []
for i in range(len(a)):
    lis2.append((a[i], 800))
for i in range(len(c)):
    lis2.append((c[i], 200))

dic = dict(lis)
dic2 = dict(lis2)

plt.figure(figsize=(40, 40), dpi=250)

G = nx.DiGraph()

for _, row in grn_data.iterrows():
    gene1 = row['TF']
    gene2 = row['target']
    importance = row['importance']

    # 添加边，并设置权重
    G.add_edge(gene1, gene2, weight=importance)

node_color_values = [dic[gene1] for gene1 in G.nodes]
node_sizes = [dic2[gene] for gene in G.nodes]

# pos = nx.spring_layout(G)
pos = nx.kamada_kawai_layout(G)

edges = G.edges()
weights = [G[u][v]['weight'] for u, v in edges]
print(weights)
nx.draw(G, pos, node_size=node_sizes, node_color=node_color_values, edge_color=weights, edge_cmap=plt.cm.PuBuGn,
        width=15)
# 使用 nx.draw_networkx_edges 绘制边
# nx.draw_networkx_edges(G, pos, edge_color=weights, edge_cmap=plt.cm.PuBuGn, width=2)
#
# # 使用 nx.draw_networkx_nodes 绘制节点
# nx.draw_networkx_nodes(G, pos, node_size=node_sizes, node_color=node_color_values)


# plt.savefig("/path/on/server/data/GRN.png", format="PNG", dpi=250, bbox_inches='tight', pad_inches=0)
plt.savefig(data_path + "GRN.png", format="PNG", dpi=250, bbox_inches='tight', pad_inches=0)
