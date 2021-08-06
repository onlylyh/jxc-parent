package utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageResultUtils {

    public static Map<String,Object> getPageResult(Long total, List<?> records){
        Map<String,Object> map = new HashMap<>();
        map.put("count",total);
        map.put("data",records);
        return map;
    }
}
