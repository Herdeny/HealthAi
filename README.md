# Health AI

## 环境配置

### 项目配置

项目主体部分 基于 open-jdk 21 开发

项目配置文件位于`src/main/resources/`

prod 版本为远程部署文件  dev 版本为本地部署文件

使用时应先将 src/main/resources/application.properties 内的

```
spring.profiles.active=dev
```

属性修改为适应的版本，同时修改对应的配置文件

### Python环境

Python脚本文件环境目录位于 `src/main/python/environment.yml`

基于环境目录创建环境，可以使用以下指令：

```bash
conda env create -f environment.yml
```

## 项目结构

```
HealthAI
├─ mvnw
├─ mvnw.cmd
├─ pom.xml
├─ README.md
├─ src
│  └─ main
│     ├─ resources
│     │  ├─ application-dev.properties		——本地部署配置文件
│     │  ├─ application-prod.properties		——远程部署配置文件
│     │  └─ application.properties		——配置配置文件
│     └─ python
│        ├─ AD-GRN.py		——AD-GRN（csv2loom）
│        ├─ createImg.py		——AD-GRN（loom2pig）
│        ├─ createReport.py		——生成报告
│        ├─ createROC.py		——绘制ROC曲线
│        ├─ environment.yml		——环境文件
│        └─ GeneAnalysis.py		——疾病预测
├─ model
│  ├─ **best_model_tempro-spatialfusion_Braak.h5**		——疾病预测及ROC曲线生成
│  ├─ **best_model_tempro-spatialfusion_CERAD.h5**		——疾病预测及ROC曲线生成
│  ├─ **best_model_tempro-spatialfusion_Cogdx.h5**		——疾病预测及ROC曲线生成
│  ├─ **final_spatial_fusion.csv**		——疾病预测及ROC曲线生成
│  ├─ **hg38\__refseq-r80__10kb_up_and_down_tss.mc9nr.genes_vs_motifs.rankings.feather**		——AD-GRN
│  ├─ **hs_hgnc_tfs.txt**		——AD-GRN
│  ├─ **label.csv**		——疾病预测及ROC曲线生成
│  ├─ **label_mapping.csv**		——疾病预测及ROC曲线生成
│  └─ **motifs-v9-nr.hgnc-m0.001-o0.0.tbl**		——AD-GRN
├─ model
│  ├─ *ACT_377_4830.csv*		——AD-GRN测试文件
│  ├─ ......
│  └─ test_data		——疾病预测及ROC曲线生成测试文件
│     ├─ *GSM4432645_EC2.csv*
│     ├─ *GSM4432647_EC3.csv*
│     ├─ *GSM4432648_EC4.csv*
│     ├─ *GSM4432649_EC6.csv*
│     ├─ *GSM4432650_EC7.csv*
│     ├─ *GSM4432651_EC5.csv*
│     ├─ *GSM4432652_EC9.csv*
│     ├─ *GSM4432653_EC8.csv*
│     └─ *GSM4432654_EC10.csv*
│  └─ *......*
└─ .mvn
   └─ wrapper
      └─ maven-wrapper.properties
```



## 测试数据说明

//TODO

