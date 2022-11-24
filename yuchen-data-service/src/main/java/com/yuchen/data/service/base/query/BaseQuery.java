package com.yuchen.data.service.base.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @description: TODO 所有查询类的基类
 * @title: BaseQuery
 * @projectName parent
 * @author lizhiwei
 * @date 2019/6/17 14:50
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "分页参数VO")
public abstract class BaseQuery<T>  {
    /** start from 0*/
    @ApiModelProperty(value = "页码")
    protected int pageIndex = 0;
    @ApiModelProperty(value = "页面大小")
    protected int pageSize = 10;
    private static Map<Class, List<Field>> fieldCache = new HashMap<>();
    /**
     * 将查询转换成Specification
     * @return
     */
    public abstract Specification<T> toSpec();
    /**
     * JPA分页查询类
     */
    public Pageable toPageable() {
    	return PageRequest.of(pageIndex-1, pageSize);
    }
    /**
     * JPA分页查询类,带排序条件
     */
    public Pageable toPageable(Sort sort) {
        return PageRequest.of(pageIndex-1, pageSize, sort);
    }
    /**
     * 动态查询and连接
     */
    protected Specification<T> toSpecWithAnd() {
        return this.toSpecWithLogicType("and");
    }
    /**
     *动态查询or连接
     */
    protected Specification<T> toSpecWithOr() {
        return this.toSpecWithLogicType("or");
    }

    /**
     *logicType or/and
     */
    private Specification<T> toSpecWithLogicType(final String logicType) {
        final BaseQuery outerThis = this;
        return (root, criteriaQuery, cb) -> {
            Class clazz = outerThis.getClass();
            //判断缓存中是否已经存在，存在不需要再次生成，不存在需要重新生成
            //获取查询类Query的所有字段,包括父类字段
            List<Field> fields = getAllFieldsWithRoot(clazz);
            List<Predicate> predicates = new ArrayList<>(fields.size());
            for (Field field : fields) {
                //获取字段上的@QueryWord注解
                QueryWord qw = field.getAnnotation(QueryWord.class);
                if (qw == null){
                    continue;
                }
                // 获取字段名
                String column = qw.column();
                String c = "";
                //如果主注解上colume为默认值"",则以field为准
                if (column.equals(c)){
                    column = field.getName();
                    field.setAccessible(true);
                }
                try {
                    // nullable
                    Object value = field.get(outerThis);
                    //如果值为null,注解未标注nullable,跳过
                    if (value == null && !qw.nullable()) {
                        continue;
                    }
                    // can be empty
                    if (value != null && String.class.isAssignableFrom(value.getClass())) {
                        String s = (String) value;
                        String b = "";
                        //如果值为"",且注解未标注emptyable,跳过
                        if (s.equals(b) && !qw.emptiable()){
                            continue;
                        }
                    }

                    //通过注解上func属性,构建路径表达式
                    Path path = root.get(column);
                    switch (qw.func()) {
                        case equal:
                            predicates.add(cb.equal(path, value));
                            break;
                        case like:
                            predicates.add(cb.like(path, "%" + value + "%"));
                            break;
                        case gt:
                            predicates.add(cb.gt(path, (Number) value));
                            break;
                        case lt:
                            predicates.add(cb.lt(path, (Number) value));
                            break;
                        case ge:
                            predicates.add(cb.ge(path, (Number) value));
                            break;
                        case le:
                            predicates.add(cb.le(path, (Number) value));
                            break;
                        case notEqual:
                            predicates.add(cb.notEqual(path, value));
                            break;
                        case notLike:
                            predicates.add(cb.notLike(path, "%" + value + "%"));
                            break;
                        case greaterThan:
                            predicates.add(cb.greaterThan(path, (Comparable) value));
                            break;
                        case greaterThanOrEqualTo:
                            predicates.add(cb.greaterThanOrEqualTo(path, (Comparable) value));
                            break;
                        case lessThan:
                            predicates.add(cb.lessThan(path, (Comparable) value));
                            break;
                        case lessThanOrEqualTo:
                            predicates.add(cb.lessThanOrEqualTo(path, (Comparable) value));
                            break;
                        case between:
                            switch (qw.type()) {
                                case datetime:
                                    List<Date> dateList = (List<Date>) value;
                                    predicates.add(cb.between(path, dateList.get(0), dateList.get(1)));
                                    break;
                                case number_long:
                                    List<Long> longList = (List<Long>) value;
                                    predicates.add(cb.between(path, longList.get(0), longList.get(1)));
                                    break;
                                case number_integer:
                                    List<Integer> integerList = (List<Integer>) value;
                                    predicates.add(cb.between(path, integerList.get(0), integerList.get(1)));
                                    break;
                                default:
                                    break;
                            }
                        default:
                            break;
                    }
                } catch (Exception e) {
                    continue;
                }
            }
            Predicate p = null;
            String a = "";
            String and = "and";
            String or = "or";

            if (logicType == null || logicType.equals(a) || logicType.equals(and)) {
                //and连接
                p = cb.and(predicates.toArray(new Predicate[predicates.size()]));
            } else if (logicType.equals(or)) {
                //or连接
                p = cb.or(predicates.toArray(new Predicate[predicates.size()]));
            }
            return p;
        };
    }

    /**
     *获取类clazz的所有Field，包括其父类的Field
     */
    private List<Field> getAllFieldsWithRoot(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<>();
        //获取本类所有字段
        Field[] dFields = clazz.getDeclaredFields();
        if (null != dFields && dFields.length > 0) {
            fieldList.addAll(Arrays.asList(dFields));
        }
        // 若父类是Object，则直接返回当前Field列表
        Class<?> superClass = clazz.getSuperclass();
        if (null != dFields && superClass == Object.class){
            return Arrays.asList(dFields);
        }

        // 递归查询父类的field列表
        List<Field> superFields = getAllFieldsWithRoot(superClass);
        if (null != superFields && !superFields.isEmpty()) {
            //不重复字段
            superFields.stream().
                    filter(field -> !fieldList.contains(field)).
                    forEach(field -> fieldList.add(field));
        }
        return fieldList;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
