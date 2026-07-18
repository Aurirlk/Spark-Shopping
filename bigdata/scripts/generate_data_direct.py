#!/usr/bin/env python3
"""直接生成并执行大数据测试SQL（绕开文件编码问题）"""
import subprocess, random, uuid, tempfile
from datetime import datetime, timedelta

NOW = datetime.now()
MYSQL_CMD = 'mysql -u root -p1234 --default-character-set=utf8mb4 shopping'

def run_sql(sql):
    """执行单条SQL"""
    f = tempfile.NamedTemporaryFile(mode='w', suffix='.sql', delete=False, encoding='utf-8')
    f.write(sql)
    f.close()
    r = subprocess.run(f'cmd /c "{MYSQL_CMD} < {f.name}"', capture_output=True, text=True, timeout=300, shell=True)
    import os; os.unlink(f.name)
    if r.returncode != 0 and 'ERROR' in r.stdout:
        print(f'  ❌ {r.stdout[:150]}')
        return False
    return True

# 配置
NAMES = "张伟,王芳,李娜,刘洋,陈静,杨磊,赵丽,黄强,周敏,吴涛".split(",")
ADDRS = [("北京市","朝阳区","建国路88号"),("上海市","浦东新区","陆家嘴环路1000号"),("广州市","天河区","天河路385号"),("深圳市","南山区","科技园南路1号")]
PRODUCTS = [
    (6,"iPhone 15 Pro Max",9999),(6,"iPhone 15 Pro",8999),(6,"华为 Mate 60 Pro",6999),(6,"小米14 Ultra",5999),
    (6,"三星 S24 Ultra",7999),(7,"AirPods Pro 2",1999),(7,"索尼 WH-1000XM5",2499),(7,"漫步者 W820NB",399),
    (8,"MacBook Pro 14",14999),(8,"联想小新Pro16",5999),(8,"戴尔XPS 13",8999),(10,"海尔冰箱 BCD-218",2999),
    (10,"美的空调 1.5匹",2899),(11,"小天鹅洗衣机 TG100",1999),(12,"耐克 Air Max 270",699),(12,"阿迪达斯 Ultraboost",899),
]

print("="*50)
print("🚀 大数据测试数据生成器（直连模式）")
print("="*50)

# 1. 清空
run_sql("SET FOREIGN_KEY_CHECKS=0;")
for t in ["product_review","user_behavior_log","order_detail","`order`","cart"]:
    run_sql(f"TRUNCATE {t};")
run_sql("DELETE FROM `user`; DELETE FROM product WHERE id>0;")
run_sql("SET FOREIGN_KEY_CHECKS=1;")

# 2. 商品
print("\n⏳ 商品...")
vals = []
for i,(cat,name,price) in enumerate(PRODUCTS):
    orig = round(price * random.uniform(1.1,1.5),2)
    vals.append(f"({i+1},'{name}',{cat},{price},{orig},{random.randint(100,2000)},{random.randint(10,5000)},1,NOW())")
run_sql(f"INSERT INTO product (id,name,category_id,price,original_price,stock,sales,status,create_time) VALUES {','.join(vals)};")
PID_MAX = len(PRODUCTS)
print(f"  ✅ {PID_MAX} 个商品")

# 3. 用户
print("\n⏳ 用户...")
for batch in range(5):  # 500 users
    vals = []
    for i in range(100):
        uid = batch * 100 + i + 1
        d = NOW - timedelta(days=random.randint(1,365))
        vals.append(f"('user_{uid}','$2a$10$lS4nrUR11lmWCmT8Mk62Deywkg4WfcrTzTJJk2.ca8sm1BqUugZAS','{random.choice(NAMES)}','13{random.randint(100000000,999999999)}','u{uid}@shop.com',{random.randint(0,2)},1,'{d.strftime('%Y-%m-%d %H:%M:%S')}')")
    run_sql(f"INSERT INTO `user` (username,password,nickname,phone,email,gender,status,create_time) VALUES {','.join(vals)};")
print(f"  ✅ 500 个用户")

# 4. 订单（分批次）
print("\n⏳ 订单 15000 条...")
BATCH = 200
for batch_start in range(1, 15001, BATCH):
    orders = []
    details = []
    for oid in range(batch_start, min(batch_start+BATCH-1, 15000)+1):
        uid = random.randint(1,500)
        d = NOW - timedelta(days=random.randint(1,365))
        items = random.choices(range(1,PID_MAX+1), k=random.choices([1,2,3],weights=[30,40,30])[0])
        total = 0.0
        for pid in items:
            qty = random.choices([1,1,2,2,3],weights=[30,30,20,15,5])[0]
            price = PRODUCTS[pid-1][2]
            subtotal = round(price * qty, 2)
            total += subtotal
            details.append(f"({oid},{pid},(SELECT name FROM product WHERE id={pid}),(SELECT main_image FROM product WHERE id={pid}),{price},{qty},{subtotal},'{d.strftime('%Y-%m-%d %H:%M:%S')}')")
        freight = round(random.uniform(0,20),2) if random.random()<0.3 else 0
        status = random.choices([0,1,2,3,4],weights=[5,10,15,50,20])[0]
        addr = random.choice(ADDRS)
        pt = f"'{d.strftime('%Y-%m-%d %H:%M:%S')}'" if status>=1 else "NULL"
        orders.append(f"('ORD{d.strftime('%Y%m%d%H%M%S')}{random.randint(1000,9999)}',{uid},{round(total,2)},{round(total+freight,2)},{freight},{random.choices([1,2],weights=[80,20])[0]},{status},'{random.choice(NAMES)}','13{random.randint(100000000,999999999)}','{addr[0]}{addr[1]}{addr[2]}',{pt},'{d.strftime('%Y-%m-%d %H:%M:%S')}')")
    
    sql = f"INSERT INTO `order` (order_no,user_id,total_amount,pay_amount,freight_amount,pay_type,status,receiver_name,receiver_phone,receiver_address,pay_time,create_time) VALUES {','.join(orders)};"
    run_sql(sql)
    run_sql(f"INSERT INTO order_detail (order_id,product_id,product_name,product_image,price,quantity,total_amount,create_time) VALUES {','.join(details)};")
    
    if batch_start % 2000 == 1:
        print(f"  → {batch_start}/15000")

print(f"  ✅ 15000 订单")

# 5. 行为日志
print("\n⏳ 行为日志 60000 条...")
for batch in range(6):
    vals = []
    for i in range(10000):
        uid = random.randint(1,500)
        pid = random.randint(1,PID_MAX)
        d = NOW - timedelta(days=random.randint(1,90))
        btype = random.choices([1,2,3,4,5],weights=[40,15,15,20,10])[0]
        device = random.choice(['pc','mobile','miniapp'])
        sess = f"sess_{uuid.uuid4().hex[:12]}"
        vals.append(f"({uid},{pid},(SELECT category_id FROM product WHERE id={pid}),{btype},'{device}','{random.randint(1,255)}.{random.randint(0,255)}.{random.randint(0,255)}.{random.randint(0,255)}','{sess}','{d.strftime('%Y-%m-%d %H:%M:%S')}')")
    run_sql(f"INSERT INTO user_behavior_log (user_id,product_id,category_id,behavior_type,device_type,ip_address,session_id,create_time) VALUES {','.join(vals)};")
    print(f"  → {(batch+1)*10000}/60000")
print(f"  ✅ 60000 行为日志")

# 6. 评价
print("\n⏳ 评价 12000 条...")
reviews = ["商品质量很好！","性价比很高","非常满意","包装精美","价格实惠","发货速度快","质量不错","外观漂亮","使用方便","一般般"]
for batch in range(3):
    vals = []
    for i in range(4000):
        d = NOW - timedelta(days=random.randint(1,180))
        rating = random.choices([1,2,3,4,5],weights=[3,5,15,35,42])[0]
        vals.append(f"({random.randint(1,PID_MAX)},{random.randint(1,500)},NULL,{rating},'{random.choice(reviews)}',{1 if random.random()<0.15 else 0},'{d.strftime('%Y-%m-%d %H:%M:%S')}')")
    run_sql(f"INSERT INTO product_review (product_id,user_id,order_id,rating,content,is_anonymous,create_time) VALUES {','.join(vals)};")
print(f"  ✅ 12000 评价")

# 7. 验证
print("\n" + "="*50)
print("📊 数据验证")
print("="*50)
for name,table in [("用户","`user`"),("商品","product"),("订单","`order`"),("明细","order_detail"),("行为日志","user_behavior_log"),("评价","product_review")]:
    f = tempfile.NamedTemporaryFile(mode='w', suffix='.sql', delete=False, encoding='utf-8')
    f.write(f"SELECT '{name}',COUNT(*) FROM {table};")
    f.close()
    r = subprocess.run(f'cmd /c "{MYSQL_CMD} < {f.name}"', capture_output=True, text=True, timeout=30, shell=True)
    import os; os.unlink(f.name)
    lines = [l for l in r.stdout.split('\n') if l.strip() and 'Warning' not in l and 'COUNT' not in l]
    for l in lines:
        if l.strip():
            print(f"  {l.strip()}")

print("\n🎉 大数据测试数据生成完成！")
