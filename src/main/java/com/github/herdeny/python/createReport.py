import datetime

from PIL import Image as PILImage
from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import inch
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.cidfonts import UnicodeCIDFont
from reportlab.pdfgen import canvas as Canvas
from reportlab.platypus import Image, SimpleDocTemplate, Spacer, Paragraph, Table, TableStyle

# 设置字体
pdfmetrics.registerFont(UnicodeCIDFont('STSong-Light'))
song = 'STSong-Light'

# 增加图像像素限制
PILImage.MAX_IMAGE_PIXELS = None

# 报告尺寸限制
PAGE_HEIGHT = A4[1]
PAGE_WIDTH = A4[0]

# 日期
report_date = datetime.date.today()

# 创建文档
doc = SimpleDocTemplate("pathology_report.pdf", pagesize=A4)

# 样式表
styles = getSampleStyleSheet()
styleN = ParagraphStyle(name="SimSunNormal", fontName=song, fontSize=12, leading=14)
styleH = ParagraphStyle(name="SimSunHeading", fontName=song, fontSize=18, leading=22, spaceAfter=10)
styleSubH = ParagraphStyle(name="SimSubSunHeading", fontName=song, fontSize=16, leading=22, spaceAfter=10)
styleCenter = ParagraphStyle(name="Center", fontName=song, fontSize=12, leading=14, alignment=TA_CENTER)

# 图片和描述
images = ['1.png', '2.png', '3.png']  # 图片路径
descriptions = ['ROC曲线', 'PR曲线', '小提琴图']  # 对应的图片描述


# 页面标题
def report_page(c: Canvas, doc):
    c.saveState()
    c.setFillColor(colors.black)
    c.setFont(song, 30)
    c.drawCentredString(PAGE_WIDTH / 2.0, PAGE_HEIGHT - 80, "AI医疗诊断报告")
    c.setFont(song, 12)
    c.drawString(80, PAGE_HEIGHT - 120, f"日期: {report_date}")
    c.drawString(80, PAGE_HEIGHT - 140, "报告编号: 001")
    # 绘制线条
    c.line(70, PAGE_HEIGHT - 150, 520, PAGE_HEIGHT - 150)
    # 绘制页脚
    DrawPageFoot(c)
    c.restoreState()


def myLaterPages(c: Canvas, doc):
    """
    除第一页外其它页的页眉页脚配置
    :param self:
    :param canvas:
    :param doc:
    :return:
    """
    c.saveState()
    DrawPageHead(c)
    DrawPageFoot(c)
    c.restoreState()


def DrawPageHead(c: Canvas):
    """绘制页眉"""
    # 设置页眉的字体、大小、颜色
    c.setFont(song, 8)
    c.setFillColor(colors.black)
    # 绘制标题（页眉居中显示）
    c.drawCentredString(PAGE_WIDTH / 2, PAGE_HEIGHT - 50, "AI医疗诊断报告")
    # 设置页眉线条颜色和样式
    c.setStrokeColor(colors.dimgrey)
    c.setLineWidth(0.5)
    c.line(30, PAGE_HEIGHT - 60, PAGE_WIDTH - 30, PAGE_HEIGHT - 60)


def DrawPageFoot(c: Canvas, date=datetime.date.today):
    """绘制页脚"""
    # 设置边框颜色
    c.setStrokeColor(colors.dimgrey)
    # 绘制线条
    c.line(30, PAGE_HEIGHT - 790, 570, PAGE_HEIGHT - 790)
    # 绘制页脚文字
    c.setFont(song, 8)
    c.setFillColor(colors.black)
    c.drawString(30, PAGE_HEIGHT - 810, f"报告生成日期：{report_date}，Health_AI提供技术支持")


# 添加报告部分
# 报告内容
Story = [Spacer(1, inch), Paragraph("诊断结果", ParagraphStyle(
    name="SimSunHeading", fontName=song, fontSize=18, leading=22, spaceBefore=10,spaceAfter=5
))]

# 添加表格示例（病理学数据）
data = [
    ['诊断项', '值', '说明'],
    ['预测疾病阶段', '3', '预测为疾病的第3阶段'],
    ['准确率', '85%', '预测的整体准确率'],
    ['精确度', '88%', '实际为正的比例'],
    ['召回率', '80%', '被正确预测为正的比例'],
    ['F1分数', '84%', '模型的综合表现']
]

# 设置表格样式
table = Table(data, colWidths=[2 * inch, 1.5 * inch, 2.5 * inch])
table.setStyle(TableStyle([
    ('BACKGROUND', (0, 0), (-1, 0), colors.grey),
    ('TEXTCOLOR', (0, 0), (-1, 0), colors.whitesmoke),
    ('ALIGN', (0, 0), (-1, -1), 'CENTER'),
    ('FONTNAME', (0, 0), (-1, -1), song),
    ('FONTSIZE', (0, 0), (-1, -1), 12),
    ('BOTTOMPADDING', (0, 0), (-1, 0), 12),
    ('BACKGROUND', (0, 1), (-1, -1), colors.beige),
    ('GRID', (0, 0), (-1, -1), 1, colors.black),
]))

# 添加表格到报告
Story.append(table)
Story.append(Spacer(1, 0.5 * inch))

Story.append(Paragraph("图像分析:", styleSubH))


# 定义每行放置的图片数量
images_per_row = 1  # 每行1张图片

# 创建一个列表来存放每行的图片和描述
table_data = []

# 添加图片和描述
for img_path, desc in zip(images, descriptions):
    # 加载图片并调整大小
    img = PILImage.open(img_path)
    aspect_ratio = img.width / img.height

    img_width = 310  # 设置图片宽度
    img_height = img_width / aspect_ratio  # 保持比例调整高度
    report_img = Image(img_path, width=img_width, height=img_height)

    # 在一个单元格内添加图片和描述（图片在上，描述在下）
    cell_content = [report_img, Spacer(1, 0.1 * inch), Paragraph(desc, styleCenter)]

    # 将图片和描述一起作为一个单元格添加到行中
    row = [cell_content]
    table_data.append(row)

# 使用 Table 来生成表格布局
image_table = Table(table_data)

# 添加表格样式，设置图片之间的间距
image_table.setStyle(TableStyle([
    ('ALIGN', (0, 0), (-1, -1), 'CENTER'),  # 所有单元格水平居中对齐
    ('VALIGN', (0, 0), (-1, -1), 'TOP'),  # 单元格内容垂直靠上
    ('BOTTOMPADDING', (0, 0), (-1, -1), 12),  # 设置图片下方间距
    ('LEFTPADDING', (0, 0), (-1, -1), 10),  # 左侧间距
    ('RIGHTPADDING', (0, 0), (-1, -1), 10),  # 右侧间距
]))

# 将表格添加到报告中
Story.append(image_table)

# 保存文档
doc.build(Story, onFirstPage=report_page, onLaterPages=myLaterPages)
