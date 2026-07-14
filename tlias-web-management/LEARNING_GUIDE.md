# TLIAS 教学管理系统 — 后端开发学习指南

> 适用人群：Spring Boot + MyBatis 初学者
> 学习方式：按照阶段顺序阅读，每个阶段先理解"为什么"再动手"怎么做"

---

## 整体架构类比

```
浏览器（前端）          后端（我们写的）          数据库
┌──────────┐          ┌──────────────┐          ┌────────┐
│  Vue/React │ --请求--> │ Controller   │ --调用--> │        │
│  管理界面  │          │   （前台接待）  │          │ MySQL  │
│           │ <--响应-- │              │ <--查询-- │        │
└──────────┘          │ Service      │          └────────┘
                      │   （业务经理）  │
                      │ Mapper       │
                      │   （数据仓库）  │
                      └──────────────┘
```

**类比**：后端就像一家餐厅——
- **Controller** = 服务员（接单、传菜）
- **Service** = 厨师（处理食材、烹饪）
- **Mapper** = 仓库管理员（存取原材料）
- **Pojo/Entity** = 菜品模具（定义数据长什么样）

---

## 阶段一：环境准备与项目启动

### 1.1 项目结构概览

```
tlias-web-management/
└── src/main/
    ├── java/org/example/
    │   ├── TliasWebManagementApplication.java  ← 启动入口
    │   ├── controller/   ← 服务员：接收HTTP请求
    │   ├── service/      ← 厨师：业务逻辑
    │   │   └── impl/     ← 厨师的具体实现
    │   ├── mapper/       ← 仓库管理员：数据库操作
    │   ├── pojo/         ← 数据模型：菜品的形状
    │   ├── config/       ← 配置：餐厅的规矩
    │   └── exception/    ← 异常处理：出问题了怎么办
    └── resources/
        ├── application.yml           ← 全局配置（数据库连接等）
        └── org/example/mapper/       ← XML映射文件（复杂SQL）
```

### 1.2 数据流向 — "请求的一生"

```
用户点击"查询班级"
    │
    ▼
① 前端发出 GET http://localhost:8080/clazzs
    │
    ▼
② ClazzController.list()  收到请求（服务员接单）
    │  @GetMapping 匹配 GET 请求
    │  调用 clazzService.list()
    ▼
③ ClazzServiceImpl.list()  处理业务（厨师做菜）
    │  调用 clazzMapper.list()
    ▼
④ ClazzMapper.xml 执行 SQL（仓库取原材料）
    │  SELECT c.*, e.name as masterName ...
    │  FROM clazz c LEFT JOIN emp e ...
    ▼
⑤ MySQL 返回数据行
    │
    ▼
⑥ MyBatis 将行数据映射为 List<Clazz>（装盘）
    │
    ▼
⑦ Controller 包装为 Result.success(data)（端给客人）
    │
    ▼
⑧ 浏览器收到 JSON，渲染表格
```

**关键理解**：每一层只做自己该做的事，通过接口（interface）解耦。

---

## 阶段二：跨域问题 — "为什么服务器有响应但前端不显示？"

### 2.1 问题类比

```
餐厅规则：只有坐在餐厅里的客人才能点菜
         但外卖App（前端）在外面，被门禁挡住了
         
解决：服务员说"外卖订单也可以接！" → 这就是 CORS 配置
```

### 2.2 什么是跨域（CORS）？

浏览器有一个安全规则叫**同源策略**（Same-Origin Policy）：

| 前端地址 | 后端地址 | 是否跨域 |
|---------|---------|---------|
| `localhost:5173` | `localhost:8080` | ❌ 跨域（端口不同） |
| `localhost:8080` | `localhost:8080` | ✅ 同源 |
| `example.com` | `api.example.com` | ❌ 跨域（子域名不同） |

**通俗解释**：浏览器就像一个小区的门禁系统。
- 前端（页面）在5173号门进出
- 后端（API）在8080号门
- 门禁不放行从5173去8080的请求 → 这就是跨域错误

服务器确实返回了数据（curl能收到），但浏览器拒绝把数据交给前端JS。

### 2.3 解决方案

```java
// config/WebConfig.java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")              // 所有接口都允许跨域
                .allowedOriginPatterns("*")     // 允许来自任何地址的请求
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")            // 允许任何请求头
                .allowCredentials(true);        // 允许携带Cookie
    }
}
```

**类比理解**：
```
addMapping("/**")       → "餐厅所有菜品"
allowedOriginPatterns   → "接受来自任何外卖平台的订单"
allowedMethods          → "支持点单、加菜、退菜、换菜"
```

---

## 阶段三：CRUD 基础 — 以班级管理为例

### 3.1 什么是 CRUD？

| 操作 | HTTP方法 | SQL | 类比 |
|------|---------|-----|------|
| **C**reate | POST | INSERT | 新增一道菜 |
| **R**ead | GET | SELECT | 查看菜单 |
| **U**pdate | PUT | UPDATE | 修改菜品配方 |
| **D**elete | DELETE | DELETE | 下架一道菜 |

### 3.2 Controller 层 — "服务员"

```java
@Slf4j                                    // 日志注解，等于服务员手中的点菜本
@RestController                           // 告诉Spring："这是个服务员！"
@RequestMapping("/clazzs")                // 这个服务员负责班级相关的所有请求
public class ClazzController {

    @Autowired                            // Spring自动分配帮手（厨师）
    private ClazzService clazzService;

    // GET /clazzs  → 查询全部班级
    @GetMapping                           // 只匹配GET请求
    public Result list(ClazzQueryParam param) {
        log.info("条件分页查询班级，参数：{}", param);   // 在小本本上记录
        PageResult<Clazz> pageResult = clazzService.list(param);
        return Result.success(pageResult);         // 把菜端出去
    }

    // GET /clazzs/{id}  → 查询单个班级（{id}是路径变量）
    @GetMapping("/{id}")
    public Result getInfo(@PathVariable Integer id) {
        // @PathVariable 的作用：从URL中提取{id}部分
        // 例如 /clazzs/5  →  id = 5
        log.info("查询班级详情，id：{}", id);
        Clazz clazz = clazzService.getById(id);
        return Result.success(clazz);
    }

    // POST /clazzs  → 新增班级
    @PostMapping
    public Result add(@RequestBody Clazz clazz) {
        // @RequestBody 的作用：把前端传来的JSON自动转成Clazz对象
        // {"name":"xxx","room":"101"}  →  Clazz对象
        log.info("添加班级信息：{}", clazz);
        clazzService.add(clazz);
        return Result.success();
    }

    // PUT /clazzs  → 修改班级
    @PutMapping
    public Result update(@RequestBody Clazz clazz) {
        log.info("修改班级信息：{}", clazz);
        clazzService.update(clazz);
        return Result.success();
    }

    // DELETE /clazzs/{id}  → 删除班级
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        log.info("删除班级信息，id：{}", id);
        clazzService.deleteById(id);
        return Result.success();
    }
}
```

### 3.3 Service 层 — "厨师"

```java
// 接口（菜谱）：定义能做什么
public interface ClazzService {
    PageResult<Clazz> list(ClazzQueryParam param);  // 查询班级列表
    List<Clazz> listAll();                          // 查询全部（无分页）
    Clazz getById(Integer id);                      // 按ID查一个
    void add(Clazz clazz);                          // 新增
    void update(Clazz clazz);                       // 修改
    void deleteById(Integer id);                    // 删除
}

// 实现（真正做菜的地方）
@Service                        // 告诉Spring："这是个厨师！"
public class ClazzServiceImpl implements ClazzService {

    @Autowired                  // 让Spring分配一个仓库管理员
    private ClazzMapper clazzMapper;

    @Override
    public PageResult<Clazz> list(ClazzQueryParam param) {
        // PageHelper 是分页助手，相当于"每页只取固定数量"
        PageHelper.startPage(
            param.getPage() != null ? param.getPage() : 1,        // 默认第1页
            param.getPageSize() != null ? param.getPageSize() : 10 // 默认每页10条
        );
        List<Clazz> list = clazzMapper.list(param);
        Page<Clazz> page = (Page<Clazz>) list;   // PageHelper把List包装成了Page
        return new PageResult<>(page.getTotal(), page.getResult());
    }

    @Override
    public void add(Clazz clazz) {
        clazz.setCreateTime(LocalDateTime.now());  // 自动填创建时间
        clazz.setUpdateTime(LocalDateTime.now());  // 自动填修改时间
        clazzMapper.insert(clazz);
    }
    // ... 其他方法类似
}
```

### 3.4 Mapper 层 — "仓库管理员"

**Java接口（简单查询用注解）：**
```java
@Mapper   // 告诉MyBatis："我是数据库操作接口！"
public interface ClazzMapper {
    List<Clazz> list(ClazzQueryParam param);
    List<Clazz> listAll();
    Clazz getById(Integer id);
    void insert(Clazz clazz);
    void update(Clazz clazz);
    void deleteById(Integer id);
}
```

**XML映射文件（复杂查询用XML）：**
```xml
<!-- 条件分页查询 — 动态SQL -->
<select id="list" resultType="org.example.pojo.Clazz">
    SELECT
        c.*,
        e.name AS masterName,     <!-- 别名：自动映射到Clazz.masterName -->
        CASE
            WHEN curdate() < c.begin_date THEN '未开班'
            WHEN curdate() BETWEEN c.begin_date AND c.end_date THEN '在读'
            ELSE '已结课'
        END AS status             <!-- 计算字段：根据日期判断状态 -->
    FROM clazz c
    LEFT JOIN emp e ON c.master_id = e.id   <!-- 关联查询：获取班主任姓名 -->
    <where>                        <!-- 动态WHERE：有参数才加条件 -->
        <if test="name != null and name != ''">
            AND c.name LIKE CONCAT('%', #{name}, '%')   <!-- 模糊查询 -->
        </if>
        <if test="begin != null">
            AND c.begin_date >= #{begin}
        </if>
        <if test="end != null">
            AND c.begin_date <= #{end}
        </if>
    </where>
    ORDER BY c.update_time DESC
</select>
```

**XML vs 注解 的选择原则：**
```
简单SQL（单表、无动态条件）→ 用 @Select / @Insert 注解
复杂SQL（多表JOIN、动态WHERE）→ 用 XML 映射文件
```

### 3.5 Pojo — "数据模型"

```java
@Data                     // Lombok：自动生成getter/setter/toString
@NoArgsConstructor        // 自动生成无参构造器
@AllArgsConstructor       // 自动生成全参构造器
public class Clazz {
    private Integer id;          // 主键ID
    private String name;         // 班级名称
    private String room;         // 教室
    private LocalDate beginDate; // 开课时间
    private LocalDate endDate;   // 结课时间
    private Integer masterId;    // 班主任ID（外键 → emp表）
    private Integer subject;     // 学科：1-Java,2-前端,3-大数据...

    // 以下字段不在clazz表中，通过JOIN或计算得出
    private String masterName;   // 班主任姓名（来自emp.name）
    private String status;       // 班级状态（根据日期计算）
}
```

**为什么有些字段不在数据库表中？**
```
类比：快递单号可以查到包裹在哪
      masterId → 通过JOIN找到 → masterName（班主任姓名）
      beginDate+endDate → 通过计算 → status（未开班/在读/已结课）
      
这些叫"派生字段"，存在Pojo中方便前端使用，但不实际存储在clazz表里。
```

---

## 阶段四：前端下拉框 — "班主任选择功能"

### 4.1 问题场景

```
编辑班级弹窗中有一个"班主任"下拉框：
┌─────────────────────────┐
│  班级名称：[__________]  │
│  教室：[__________]      │
│  班主任：[▼ 请选择    ]  │  ← 这个下拉框需要显示所有老师的名字
│          │ 令狐冲      │
│          │ 公孙胜      │
│          │ 卢俊义      │
│          └─────────────│
└─────────────────────────┘
```

### 4.2 下拉框需要什么数据？

```
前端下拉组件需要两样东西：
① 选项列表：[{id:36, name:"令狐冲"}, {id:5, name:"公孙胜"}, ...]
② 当前值：masterId = 10（如果正在编辑已有班级）

① 来自 → GET /emps/simple
② 来自 → GET /clazzs/{id}（返回masterId字段）
```

### 4.3 提供干净的下拉数据

```java
// EmpMapper.java — 只查需要的字段
@Select("SELECT id, name, job FROM emp ORDER BY name")
List<Map<String, Object>> findSimpleList();
// 返回 Map 而非 Emp 对象的好处：不会带一堆 null 字段
// 返回：[{id:36, name:"令狐冲", job:1}, ...]
// 而不是：[{id:36, name:"令狐冲", job:1, phone:null, salary:null, ...}]
```

**为什么用 Map 而非 Emp 对象？**
```
类比：去超市只买牛奶，不需要把整个超市搬回家
Emp对象有20+个字段，但下拉框只需要 id, name, job
用Map只返回这3个字段 → 响应更小，前端处理更快
```

---

## 阶段五：业务联动 — "员工自动升任班主任"

### 5.1 业务需求

```
当一个员工被指定为某班级的班主任时：
→ 该员工的职位(job)自动变为"班主任"(job=1)

场景：
  阮小二 原本是 job=2（讲师）
  → 被设为 JavaEE就业166期 的班主任（masterId=20）
  → 系统自动将 阮小二.job 改为 1（班主任）
```

### 5.2 实现 — 在添加/修改班级时联动

```java
// ClazzServiceImpl.java
@Transactional(rollbackFor = Exception.class)   // 事务：要么全成功，要么全回滚
@Override
public void add(Clazz clazz) {
    clazz.setCreateTime(LocalDateTime.now());
    clazz.setUpdateTime(LocalDateTime.now());
    clazzMapper.insert(clazz);          // ① 先保存班级

    // ② 如果指定了班主任，自动升级员工职位
    if (clazz.getMasterId() != null) {
        empMapper.updateJob(clazz.getMasterId(), 1);  // job=1 → 班主任
    }
}
```

**为什么要加 @Transactional？**
```
类比：银行转账
  ① 从A账户扣100元
  ② 向B账户加100元
  
如果①成功但②失败了 → 钱丢了！
@Transactional 确保：任何一步失败，全部回滚到操作前的状态

班级+职位联动也一样：
  ① 保存班级成功
  ② 更新职位失败
  → @Transactional 回滚，班级也不会被保存
```

---

## 阶段六：路由冲突 — "list 被当成 ID"

### 6.1 问题现象

```
错误日志：
  Failed to convert value of type 'String' to required type 'Integer'
  For input string: "list"
```

### 6.2 为什么发生？

```java
// ClazzController中的两个路由：
@GetMapping("/list")          // 期望匹配 GET /clazzs/list
@GetMapping("/{id}")          // 期望匹配 GET /clazzs/5

// Spring的路由匹配规则：更具体的路径优先
// GET /clazzs/list → 先匹配 /list ✓
// GET /clazzs/5    → 不匹配 /list，再匹配 /{id} → id=5 ✓
```

如果 `/list` 路由不存在：
```
GET /clazzs/list → 没有 /list 路由 → 尝试 /{id}
                                  → id = "list"（字符串！）
                                  → 无法转为 Integer
                                  → 报错！
```

### 6.3 解决

```
① 添加显式的 /list 路由（优先级高于 /{id}）
② 添加 MethodArgumentTypeMismatchException 异常处理器
   → 即使出错，也返回友好的错误信息而非500
```

---

## 阶段七：统一响应格式

### 7.1 Result 类 — "餐馆的标准化餐盘"

```java
// 不管什么菜，都用同一个盘子端出去
@Data
public class Result {
    private Integer code;   // 1=成功, 0=失败（类似于HTTP状态码的简化版）
    private String msg;     // 提示信息
    private Object data;    // 实际数据（可以是任何类型）

    public static Result success(Object data) {
        Result r = new Result();
        r.code = 1;
        r.msg = "success";
        r.data = data;
        return r;
    }

    public static Result error(String msg) {
        Result r = new Result();
        r.code = 0;
        r.msg = msg;
        return r;
    }
}
```

**为什么统一格式？**
```
没有统一格式时：
  /clazzs     → [班级数组]
  /clazzs/5   → {班级对象}
  /clazzs     → "删除成功"  （POST请求）

前端要写三套解析逻辑！

有了统一格式：
  所有接口都返回 {code, msg, data}
  前端只需要判断 code==1 然后取 data
```

### 7.2 PageResult — "分页专用盘"

```java
@Data
public class PageResult<T> {
    private Long total;       // 总记录数（数据库一共多少条）
    private List<T> rows;     // 当前页的数据
}

// 响应示例：
{
  "code": 1,
  "data": {
    "total": 53,             // 一共53个班级
    "rows": [                // 当前页10条
      {"id":1, "name":"JavaEE就业163期", ...},
      ...
    ]
  }
}
```

---

## 阶段八：异步任务初探

```java
// ClazzController 中的 /list 别名体现了前端路由设计的常见模式
@GetMapping
public Result list(ClazzQueryParam param) {        // GET /clazzs
    // 主列表页：条件筛选 + 分页 + 富数据
}

@GetMapping("/list")
public Result listAll() {                          // GET /clazzs/list
    // 纯列表：无分页 + 原始字段（用于下拉、导出等）
}
```

**为什么要设计两个列表接口？**
```
类比：
  /clazzs      → 餐厅的"菜单本"（有图片、价格、分类）→ 给顾客看
  /clazzs/list → 后厨的"备料单"（只有名称和数量）→ 给厨师用

不同场景需要不同的数据粒度，接口也相应设计。
```

---

## 整体回顾 — 完整请求链路

```
┌─ 前端操作 ────────────────────────────────────────────────────┐
│  用户打开班级管理页面，输入搜索条件"Java"，点击查询             │
└──────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─ 请求 ────────────────────────────────────────────────────────┐
│  GET /clazzs?name=Java&page=1&pageSize=5                       │
│  Headers: Origin: http://localhost:5173                        │
└──────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─ WebConfig（门禁检查）─────────────────────────────────────────┐
│  Origin: localhost:5173 ≠ localhost:8080 → 跨域！              │
│  但 allowedOriginPatterns("*") → 放行 ✓                        │
│  响应头中加入 Access-Control-Allow-Origin: *                   │
└──────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─ ClazzController.list(param)（服务员接单）─────────────────────┐
│  param.name = "Java"                                           │
│  param.page = 1                                                │
│  param.pageSize = 5                                            │
│  调用 clazzService.list(param)                                 │
└──────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─ ClazzServiceImpl.list(param)（厨师处理）──────────────────────┐
│  PageHelper.startPage(1, 5)  ← 设置分页                        │
│  调用 clazzMapper.list(param)                                  │
│  拿到 Page<Clazz> → 包装为 PageResult(total=53, rows=5条)      │
└──────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─ ClazzMapper.xml（仓库取货）───────────────────────────────────┐
│  SELECT c.*, e.name AS masterName, CASE...END AS status        │
│  FROM clazz c LEFT JOIN emp e ON c.master_id = e.id            │
│  WHERE c.name LIKE '%Java%'           ← 动态条件               │
│  ORDER BY c.update_time DESC                                   │
│  LIMIT 5 OFFSET 0                     ← PageHelper自动加       │
└──────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─ 响应 ────────────────────────────────────────────────────────┐
│  HTTP 200                                                      │
│  Access-Control-Allow-Origin: *                                │
│  {                                                             │
│    "code": 1,                                                  │
│    "msg": "success",                                           │
│    "data": {                                                   │
│      "total": 6,                                               │
│      "rows": [{                                                │
│        "id": 6,                                                │
│        "name": "JavaEE就业167期",                               │
│        "masterName": "令狐冲",     ← JOIN得来的                 │
│        "status": "已结课"          ← CASE计算得来的              │
│      }, ...]                                                   │
│    }                                                           │
│  }                                                             │
└──────────────────────────────────────────────────────────────┘
```

---

## 关键注解速查表

| 注解 | 位置 | 作用 | 类比 |
|------|------|------|------|
| `@RestController` | Controller类 | 标记为REST接口 + 自动JSON序列化 | 服务员制服 |
| `@RequestMapping("/path")` | Controller类 | 统一URL前缀 | 服务员负责的区域 |
| `@GetMapping` | Controller方法 | 匹配GET请求 | "客人要看菜单" |
| `@PostMapping` | Controller方法 | 匹配POST请求 | "客人要点菜" |
| `@PutMapping` | Controller方法 | 匹配PUT请求 | "客人要换菜" |
| `@DeleteMapping` | Controller方法 | 匹配DELETE请求 | "客人要退菜" |
| `@PathVariable` | 方法参数 | 从URL路径取值 | 从订单号取信息 |
| `@RequestBody` | 方法参数 | JSON→Java对象 | 把点菜单录入系统 |
| `@Service` | Service类 | 标记为业务层Bean | 厨师工牌 |
| `@Autowired` | 字段/方法 | 自动注入依赖 | 人事部分配帮手 |
| `@Mapper` | Mapper接口 | MyBatis映射接口 | 仓库管理员钥匙 |
| `@Transactional` | Service方法 | 事务管理 | "要么全做完，要么全撤销" |
| `@Data` | Pojo类 | Lombok生成getter/setter | 模具自动成型 |

---

## 常见问题排查指南

### Q1：前端收不到数据但curl能收到？
```
→ 99%是CORS问题 → 检查 WebConfig 是否正确配置
→ 1%是请求路径写错 → F12 Network看实际请求URL
```

### Q2：接口返回500错误？
```
→ 看IDEA控制台的错误日志
→ 常见原因：SQL语法错误、字段名不匹配、参数类型不匹配
```

### Q3：数据库字段为null？
```
→ 检查 application.yml 中 map-underscore-to-camel-case: true
→ 这个配置让数据库字段 user_name 自动映射到 Java 的 userName
```

### Q4：修改了代码但不生效？
```
→ 重启应用（spring-boot:run）
→ 如果改了XML文件，需要重新编译
```

### Q5：端口被占用？
```
→ Windows: netstat -ano | findstr ":8080" → 找到PID → taskkill
→ 或用我写的快速命令
```

---

> 学习建议：不要死记硬背代码，理解每层之间的"合同"（接口/约定）才是关键。
> Controller只知道Service能做什么（接口），不关心怎么做（实现）。
> 这种分层设计让你可以单独替换任一层而不影响其他层。
