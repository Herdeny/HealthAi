import sys
import scanpy as sc
import loompy as lp
import numpy as np

file_path = sys.argv[1]
# # 使用 sc.read_csv 读取文件
x = sc.read_csv(file_path)

# 删除 .csv 后缀
if file_path.endswith('.csv'):
    file_path = file_path[:-4]

row_attrs = {"Gene": np.array(x.var_names)}
col_attrs = {"CellID": np.array(x.obs_names)}
lp.create(file_path + ".loom", x.X.transpose(), row_attrs, col_attrs)
print(file_path + ".loom")
