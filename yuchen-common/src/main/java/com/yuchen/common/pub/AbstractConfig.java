/*
 *      Copyright [2020] [xiaozhennan1995@gmail.com]
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 *      http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yuchen.common.pub;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.gson.internal.LinkedTreeMap;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("all")
public abstract class AbstractConfig extends ConcurrentHashMap<String, Object> implements Serializable {


    public AbstractConfig() {
    }

    public AbstractConfig(Map m) {
        super();
        if (m != null) {
            this.putAll(m);
        }
    }

    public void setVal(String key, Object value) {
        this.put(key, value);
    }


    public void setStringVal(String key, String value) {
        setVal(key, value);
    }

    public void setBooleanVal(String key, boolean value) {
        setVal(key, value);
    }

    public void setIntVal(String key, int value) {
        setVal(key, value);
    }

    public void setLongVal(String key, long value) {
        setVal(key, value);
    }

    public void setDoubleVal(String key, double value) {
        setVal(key, value);
    }

    public Object getVal(String key) {
        Object obj = this.get(key);
        if (obj instanceof LinkedTreeMap) {
            LinkedTreeMap treeMap = (LinkedTreeMap) obj;
            Map<String, Object> newMap = new HashMap<>(treeMap.size());
            newMap.putAll(treeMap);
            return newMap;
        }
        return obj;
    }

    public Object getVal(String key, Object defaultValue) {
        Object ret = getVal(key);
        if (ret == null) {
            return defaultValue;
        }
        return ret;
    }


    public String getStringVal(String key) {
        return (String) this.get(key);
    }

    public String getStringVal(String key, String defaultValue) {
        String ret = getStringVal(key);
        if (ret == null || ret.trim().length() == 0) {
            return defaultValue;
        }
        return ret;
    }

    public int getIntVal(String key, int defaultValue) {
        Object ret = this.get(key);
        if (ret == null) {
            return defaultValue;
        }
        if (ret instanceof Integer) {
            return ((Integer) ret).intValue();
        }
        if (ret instanceof String) {
            return Integer.valueOf((String) ret).intValue();
        }
        if (ret instanceof Long) {
            return ((Long) ret).intValue();
        }
        if (ret instanceof Float) {
            return ((Float) ret).intValue();
        }
        if (ret instanceof Double) {
            return ((Double) ret).intValue();
        }
        if (ret instanceof BigInteger) {
            return ((BigInteger) ret).intValue();
        }
        if (ret instanceof BigDecimal) {
            return ((BigDecimal) ret).intValue();
        }
        throw new RuntimeException("can't cast " + key + " from " + ret.getClass().getName() + " to Integer");
    }

    public long getLongVal(String key, long defaultValue) {
        Object ret = this.get(key);
        if (ret == null) {
            return defaultValue;
        }
        if (ret instanceof Long) {
            return ((Long) ret);
        }
        if (ret instanceof Integer) {
            return ((Integer) ret).longValue();
        }
        if (ret instanceof String) {
            return Long.valueOf((String) ret);
        }
        if (ret instanceof Float) {
            return ((Float) ret).longValue();
        }
        if (ret instanceof Double) {
            return ((Double) ret).longValue();
        }
        if (ret instanceof BigInteger) {
            return ((BigInteger) ret).longValue();
        }
        if (ret instanceof BigDecimal) {
            return ((BigDecimal) ret).longValue();
        }
        throw new RuntimeException("can't cast " + key + " from " + ret.getClass().getName() + " to Long");
    }

    public double getDoubleVal(String key, double defaultValue) {
        Object ret = this.get(key);
        if (ret == null) {
            return defaultValue;
        }
        if (ret instanceof Double) {
            return ((Double) ret);
        }
        if (ret instanceof Long) {
            return ((Long) ret).doubleValue();
        }
        if (ret instanceof Integer) {
            return ((Integer) ret).doubleValue();
        }
        if (ret instanceof String) {
            return Double.valueOf((String) ret);
        }
        if (ret instanceof Float) {
            return ((Float) ret).doubleValue();
        }
        if (ret instanceof BigInteger) {
            return ((BigInteger) ret).doubleValue();
        }
        if (ret instanceof BigDecimal) {
            return ((BigDecimal) ret).doubleValue();
        }
        throw new RuntimeException("can't cast " + key + " from " + ret.getClass().getName() + " to Double");
    }


    public boolean getBooleanVal(String key, boolean defaultValue) {
        Object ret = this.get(key);
        if (ret == null) {
            return defaultValue;
        }
        if (ret instanceof Boolean) {
            return (Boolean) ret;
        }
        if (ret instanceof String) {
            return Boolean.valueOf(((String) ret).toLowerCase());
        }
        throw new RuntimeException("can't cast " + key + " from " + ret.getClass().getName() + " to Boolean");
    }

    public List<String> getListForSplit(String key, String split) {
        List<String> vars = new ArrayList<>();
        if (key != null && !"".equalsIgnoreCase(key)) {
            Object obj = this.get(key);
            if (obj instanceof String) {
                String[] splits = ((String) obj).split(split);
                vars.addAll(Arrays.asList(splits));
            }
        }
        return vars;
    }


    public static void merge(Map oldMap, Map newMap) {
        for (Object newKey : newMap.keySet()) {
            Object o = oldMap.get(newKey);
            //不存在,直接放入
            if (o == null) oldMap.put(newKey, newMap.get(newKey));

            if (o instanceof Map) {
                merge((Map<String, Object>) o, (Map<String, Object>) newMap.get(newKey));
            } else if (o instanceof List) {
                continue;
            } else {
                if (newMap.get(newKey) != null) {
                    oldMap.put(newKey, newMap.get(newKey));
                }
            }
        }
    }


    public void print() {
        String content = JSON.toJSONString(this, SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat);
        System.out.println(content);
    }

    public <T> T getOption(Options<T> options) {
        if (options == null) return null;
        Object o = this.get(options.getKey());
        T var = null;
        if (o != null) {
            try {
                Class<T> type = options.getType();
                //如果是enum类型单独处理
                if (type.isEnum() && o instanceof String) {
                    Class<Enum> e = (Class<Enum>) type;
                    String name = (String) o;
                    Enum[] es = e.getEnumConstants();
                    for (Enum anEnum : es) {
                        if (anEnum.name().equalsIgnoreCase(name)) {
                            return (T) anEnum;
                        }
                    }
                }
                if (o instanceof Integer && type == Long.class) {
                    return (T) new Long(String.valueOf(o));
                }
                var = (T) o;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return var == null ? options.getDefaultVar() : var;
    }

    public Map<String, Object> getMap(String key) {
        Object o = this.get(key);
        Map<String, Object> result = new HashMap<>();
        if (o instanceof Map) {
            Map map = (Map) o;
            for (Object mapKey : map.keySet()) {
                result.put(mapKey.toString(), map.get(mapKey));
            }
        }
        return result;
    }


    public BaseConfig getBaseConfig(String key) {
        Object o = this.get(key);
        BaseConfig baseConfig = new BaseConfig();
        if (o instanceof Map) {
            Map map = (Map) o;
            for (Object mapKey : map.keySet()) {
                baseConfig.put(mapKey.toString(), map.get(mapKey));
            }
        }
        return baseConfig;
    }

}