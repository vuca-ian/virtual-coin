package cn.virtual.coin.domain.parser;

import cn.vuca.cloud.commons.maps.Pair;
import cn.vuca.cloud.commons.utils.StringUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author gdyang
 * @since 2025/3/2 21:36
 */
public class ValueExtractor {

    private static final Pattern PATTERN_LAMBDA_PARAMETER = Pattern.compile("(\\w+)\\s*(=|LIKE|IN)\\s*#\\{ew\\.paramNameValuePairs\\.(\\w+)\\}");
    private static final Pattern PATTERN_FUZZY_PARAMETER = Pattern.compile("%(.*?)%");
    public static<T> Object extract(T t, Field field){
        if(t instanceof Map) {
            return ((Map<?, ?>) t).get(field.getName());
        }else if(t instanceof String){
            return null;
        }else{
            return ReflectionUtils.getField(field, t);
        }
    }

    public static Pair<String,String> extractLambdaParameter(String parameter, String fieldName){
        Matcher matcher = PATTERN_LAMBDA_PARAMETER.matcher(parameter);
        while(matcher.find()) {
            String field = matcher.group(1);
            String operator = matcher.group(2);
            String variable = matcher.group(3);
            if(field.equals(fieldName)){
                return Pair.of(variable, operator);
            }
        }
        return null;
    }

    public static String extractFuzzyParameter(String parameter){
        Matcher matcher = PATTERN_FUZZY_PARAMETER.matcher(parameter);
        while(matcher.find()) {
            String originalContent = matcher.group(1);
            if (StringUtils.isNotBlank(originalContent)) {
                return originalContent;
            }
        }
        return null;
    }
}
