package cn.virtual.coin.domain.mybatis;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;

import java.util.List;

/**
 * @author gdyang
 * @since  2022/4/25 1:39 下午
 */
public class CustomSqlInjector extends DefaultSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo){
        List<AbstractMethod> methods = super.getMethodList(mapperClass, tableInfo);
        methods.add(new InsertBatchSomeColumn());
        return methods;
    }
}
