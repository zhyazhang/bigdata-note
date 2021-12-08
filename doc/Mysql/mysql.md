### MySQL 的自增 ID 用完了，在插入数据有什么问题？

可以在创建表的时候，直接声明 AUTO_INCREMENT 的初始值

```sql
create table t1(id int unsigned auto_increment primary key)  auto_increment = 4294967295;
insert into t1 values(null);
```

通过 show 命令，查看 t1 的表结构

```sql
show create table t1;
```

```
CREATE TABLE `t1` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4294967295 DEFAULT CHARSET=utf8
```

可以发现，AUTO_INCREMENT 已经变成 4294967295 了，当想再尝试插入一条数据时，得到了下面的异常结果

```
17:28:03    insert into t1 values(null) Error Code: 1062. Duplicate entry '4294967295' for key 'PRIMARY'    0.00054 sec
```

说明，当再次插入时，使用的自增 ID 还是 `4294967295`，报主键冲突的错误。

**不过，还存在另一种情况，如果在创建表没有显示申明主键，会怎么办？**

如果是这种情况，InnoDB 会自动帮你创建一个不可见的、长度为 6 字节的 row_id，而且 InnoDB 维护了一个全局的 dictsys.row_id，所以未定义主键的表都共享该 row_id，每次插入一条数据，都把全局 row_id 当成主键 id，然后全局 row_id 加 1

该全局 row_id 在代码实现上使用的是 bigint unsigned 类型，但实际上只给 row_id 留了 6 字节，这种设计就会存在一个问题：**如果全局 row_id 一直涨，一直涨，直到 2 的 48 幂次 - 1 时，这个时候再 + 1，row_id 的低 48 位都为 0，结果在插入新一行数据时，拿到的 row_id 就为 0，存在主键冲突的可能性。**

所以，为了避免这种隐患，每个表都需要定一个主键。

