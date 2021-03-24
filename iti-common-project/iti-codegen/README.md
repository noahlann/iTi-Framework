# iti-common-generator
> 基于iTi-Common库和DDD体系构建，基于领域模型对象自动生成其他非核心代码。同时兼容Mybatis-plus的代码生成（旧版）

基于模板生成代码的终结者

## 0. 运行原理
> iti-common-generator 构建于 `apt` 技术之上

框架提供若干注解和注解处理器（持续更新中……），在编译阶段，自动生成所需的各种基础类，这些基础类随着领域对象的变化而变化，从而大大减少样板代码。如有特殊需求，可直接通过子类进行扩展（可选），而无需修改Base内容。

## 1. 配置
> iti-common-generator采用两步处理，第一步由 `Maven`的`apt plugin`完成，第二步由编译器调用`apt`组件完成。

对于`maven`项目，需要添加相关依赖和插件，具体如下

```xml
<dependencies>
    <!--添加generator依赖，将自动启用 xxx 处理器（计划实现） -->
    <dependency>
        <groupId>org.lan</groupId>
        <artifactId>iti-common-generator</artifactId>
        <!--注意: 基于iti-common体系,如引入了bom包,则无需指定版本号-->
        <version>${revision}</version>
        <!--编译时有效即可，运行时，不需要引用-->
        <scope>provided</scope>
    </dependency>
</dependencies>
```

```xml
<!-- 添加apt构建插件 -->
<build>
    <plugins>
        <plugin>
            <groupId>com.mysema.maven</groupId>
            <artifactId>apt-maven-plugin</artifactId>
            <version>1.1.3</version>
            <executions>
                <execution>
                    <goals>
                        <goal>process</goal>
                    </goals>
                    <configuration>
                        <outputDirectory>target/generated-sources/java</outputDirectory>
                        <processors>
                            <!--添加 Querydsl 处理器-->
                            <processor>com.querydsl.apt.QuerydslAnnotationProcessor</processor>
                            <!--添加 DDD 处理器-->
                            <processor>org.lan.iti.codegen.NLDDDProcessororg.lan.iti.codegen.NLDDDProcessor</processor>
                        </processors>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

## 2. GenCreator
> 领域对象为限界上下文中受保护对象，绝对不应该将其暴露到外面。因此，在创建一个新的领域对象时，需要一种机制将所需数据传递到模型中。

常用的机制就是将创建时所需数据封装成一个 dto 对象，通过这个 dto 对象来传递数据，领域对象从 dto 中提取所需数据，完成对象创建工作。

creator 就是这种特殊的 Dto，在封装创建对象所需的数据的同时，提供数据到领域对象的绑定操作。

### 2.1 常规做法

假设，当前有一个`Person`类

```java
@Data
public class Person {
    private String name;
    private Date birthday;
    private Boolean enable;
}
```

我们需要创建新的 Person 对象，比较正统的方式便是，创建一个 PersonCreator，用于封装所需数据：
```java
@Data
public class PersonCreator {
    private String name;
    private Date birthday;
    private Boolean enable;
}
```

然后，在 Person 中添加创建方法，如：
```java
public static Person create(PersonCreator creator){
    Person person = new Person();
    person.setName(creator.getName());
    person.setBirthday(creator.getBirthday());
    person.setEnable(creator.getEnable());
    return person;
}
```

有几个问题：
1. `Person` 和 `PersonCreator` 包含的属性基本相同
2. 如果在 `Person` 中添加、删除、修改属性，将同时调整三处，即：`Person`/`PersonCreator`/`create方法`

对于此种机械且规律的场景，大可以采用自动化机制。

### 2.2 @GenCreator
> @GenCreator 基于此思想产生

先来看具体用法及作用

#### 2.2.1 启用注解
```java
// 添加@GenCreator注解
@GenCreator
@Data
public class Person extends IAggregateRoot<String>{
    private String id;
    private String name;
    private Date birthday;
    private Boolean enable;
}
```

#### 2.2.2 编译代码，生成`BaseXXXCreator`基类

执行`mvn clean compile`命令，在`target/generated-sources/java`的对应包下，将生成`BasePersonCreator`类，如下：
```java
@Data
public abstract class BasePersonCreator<T extends BasePersonCreator> {

  @Description("")
  private String id;

  @Description("")
  private Date birthday;

  @Description("")
  private Boolean enable;

  @Description("")
  private String name;

  public Person accept(Person target) {
    target.setId(getId());
    target.setBirthday(getBirthday());
    target.setEnable(getEnable());
    target.setName(getName());
    return target;
  }
}
```

该类含有与 Person 一样的属性，并提供 accept 方法，对 person 对象执行对应属性的 set 操作。

#### 2.2.3 构建 PersonCreator
基于 BasePersonCreator 创建 PersonCreator 类。

```java
public class PersonCreator extends BasePersonCreator<PersonCreator>{
}
```

#### 2.2.4 Person中添加静态 create 方法
使用PersonCreator 为 Person 提供的静态工厂方法

```java
@GenCreator
@Data
public class Person extends IAggregateRoot<String> {
    private String id;
    private String name;
    private Date birthday;
    private Boolean enable;

    public static Person create(PersonCreator creator){
        return creator.accept(new Person());
    }
}
```

以后 Person 属性的变化，将自动应用于 BasePersonCreator 中，程序的其他部分没有任何改变。

### 2.3 运行原理

@GenCreator 的运行原理如下：
1. 自动读取当前类的 setter 方法;
2. 筛选 **public** / **protected** / **package** 访问级别的 `setter` 方法，将其作为属性添加到 `BaseXXXCreator` 中，支持`Lombok`;
3. 创建 `accept` 方法，读取 `BaseXXXCreator` 的属性，并通过 `setter` 方法写回业务数据；

对于不需要添加到 Creator 的 setter 方法，可以使用 @GenCreatorIgnore 忽略该方法。

### 2.4 其它说明

为更进一步减少可能的样板代码编写量，@GenCreator 还提供了 genClass 选项，用于是否直接生成非`abstract`的Creator
同时，若在`聚合根`中使用`@Description`注解，也将原样写入，这是为了以后兼容`swagger`等文档库考虑。

## 3. GenConverter
> converter 主要针对使用 `jpa` 作为持久化框架的场景。

### 3.1 设计背景
Jpa 对 enum 类型默认提供了两种存储方式：
1. 存储 enum 的名称（通常为全大写）
2. 存储 enum 的定义顺序（即: original)

这两者在使用上都存在一定的问题，通常我们需要存储自定义的`code`，Jpa 提供了 `AttributeConverter` 接口用于扩展。

因此`@GenCodeBasedEnumConverter`应运而生。

### 3.2 @GenCodeBasedEnumConverter
> 基于 Code 的枚举转换器

#### 3.2.1 启用 @GenCodeBasedEnumConverter
新建枚举类，添加 `@GenCodeBasedEnumConverter` 注解
```java
@GenCodeBasedEnumConverter
public enum PersonStatus {
    ENABLE(1), DISABLE(0);

    private final int code;

    PersonStatus(int code) {
        this.code = code;
    }

    @Override
    public int getCode() {
        return this.code;
    }
}
```

#### 3.2.2 编译代码，生成 CodeBasedPersonStatusConverter
执行 `mvn clean compile` 命令，自动生成CodeBasedPersonStatusConverter
```java
public final class CodeBasedPersonStatusConverter implements AttributeConverter<PersonStatus, Integer> {
  public Integer convertToDatabaseColumn(PersonStatus i) {
    return i == null ? null : i.getCode();
  }

  public PersonStatus convertToEntityAttribute(Integer i) {
    if (i == null) return null;
    for (PersonStatus value : PersonStatus.values()){
    	if (value.getCode() == i){
    		return value; 
    	}
    }
    return null;
  }
}
```

#### 3.2.3 应用 CodeBasedPersonStatusConverter
在 Jpa 实体中使用 CodeBasedPersonStatusConverter 转化器

> 为文档编写方便，这里使用`Person`类当作Jpa实体

```java
public class Person {
    @Description("名称")
    private String name;
    @Setter(AccessLevel.PROTECTED)
    private Date birthday;
    private Boolean enable;

    @Convert(converter = CodeBasedPersonStatusConverter.class)
    private PersonStatus status;
}
```

#### 3.3.4 原理
原理应该无需描述，具体看生成的代码即可。

需要注意的是：
1. 生成器将会读取枚举类的第一个属性，故枚举类必须具有至少一个属性
2. 此属性必须具有常规getter，即getXXX，XXX为变量名称

## 4. GenRepository
> Repository 是领域驱动设计中很重要的一个组件，一个聚合根对于一个 Repository。

~~Repository 与基础设施关联紧密，框架通过 @GenSpringDataRepository 提供了 Spring Data Repository 的支持。~~

~~由于特殊考虑，暂不实现~~

### 4.1 @GenAggRepository
为聚合添加基础仓储接口`BaseXXXRepository`

在`Person` 上添加 `@GenAggRepository` 注解
```java
@GenAggRepository
@Data
public class Person extends IAggregateRoot<String> {
    @Description("唯一标识")
    private String id;
    @Description("名称")
    private String name;
    @Setter(AccessLevel.PROTECTED)
    private Date birthday;
    private Boolean enable;
    @Convert(converter = CodeBasedPersonStatusConverter.class)
    private PersonStatus status;
}
```

### 4.2 编译，生成 Base 接口
执行 `mvn clean compile`
```java
interface BasePersonRepository extends IAggregateRepository<String, Person> {
}
```

该接口实现了 `IAggregateRepository`，其为仓储提供了基础的一些`crud`方法

### 4.3 创建 PersonRepository
创建 `PersonRepository` 继承自 `BasePersonRepository`
```java
public interface PersonRepository extends BasePersonRepository{
}
```

### 4.4 创建基础设施的 仓储实现
> 需根据实际使用的仓储实现方式使用，这里简单说明jpa实现方式
```java
@Repository
public interface JpaPersonRepository extends PersonRepository, BaseEntityGraphRepository<String, PersonPo>{
}
```

该接口实现了 `PersonRepository` 与 `BaseEntityGraphRepository` ，其中

- `PersonRepository` 为领域仓储接口，一般放置于`domain.facade`
- `BaseEntityGraphRepository` 为框架基本的`jpa`基础接口，基于 EntityGraph 与 QueryDSL 扩展而来

该接口旨在实现 领域仓储接口方法 与 `jpa` 方法，其中`jpa`相关方法在注解了`@Repository`时会自动代理，无需实现。

实际使用时需根据实际需要，编写 default 方法以实现具体逻辑，如 DO与PO 的互相转换

## 5. 未完待续，将根据实际情况添加更多有用的代码生成逻辑。