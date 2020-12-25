package com.deepblue.SDKServer.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.deepblue.SDKServer.common.BaseResponse;
import com.deepblue.SDKServer.common.PageModel;
import com.deepblue.SDKServer.entity.ConsumerPurchaseRates;
import com.deepblue.SDKServer.entity.DashDeviceStatus;
import com.deepblue.SDKServer.sys.SysUserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.deepblue.SDKServer.tools.Tools.getLastDate;

@Slf4j
@Service
public class MerchantStatistic implements QueryService<ConsumerPurchaseRates> {

    private String join_condition(String sql, ConsumerPurchaseRates consumerPurchaseRates)
    {
        if(consumerPurchaseRates.getMerchantId() != null)
        {
            sql = sql + " and merchant_id = '" + consumerPurchaseRates.getMerchantId() + "'";
        }

        if(consumerPurchaseRates.getStartDate() != null)
        {
            sql = sql + " and register_time >= '" + consumerPurchaseRates.getStartDate() + "'";
        }

        if(consumerPurchaseRates.getEndDate() != null)
        {
            sql = sql + " and register_time <= '" + consumerPurchaseRates.getEndDate() + "'";
        }
        return sql;
    }

    @Autowired
    DataSource dataSource;

    public ArrayList<ConsumerPurchaseRates> QueryMerchantRates(ConsumerPurchaseRates dds) throws Exception {
        ArrayList<ConsumerPurchaseRates> list = new ArrayList<>();
        Connection connection = dataSource.getConnection();
        if(dds.getKeyType() == null || dds.getStartDate() == null || dds.getEndDate() == null)
        {
            throw new Exception("缺少参数,请注意!");
        }
        String sql = "select register_time,if_first,if_second,"+dds.getKeyType()+",register_count from hive.quixmart_analysis.consumer_purchase_rates where  1=1";
        sql = join_condition(sql,dds);
        sql += " and batch = '" + getLastDate() + "'";
        log.info(sql);

        PreparedStatement prepareStatement = connection.prepareStatement(sql);
        ResultSet resultSet = prepareStatement.executeQuery();

        while (resultSet.next()) {
            ConsumerPurchaseRates consumerPurchaseRates = new ConsumerPurchaseRates();
            consumerPurchaseRates = fillValue(dds,resultSet);
            consumerPurchaseRates.setRegisterCount(resultSet.getInt("register_count"));
            consumerPurchaseRates.setIfFirst(resultSet.getInt("if_first"));
            consumerPurchaseRates.setIfSecond(resultSet.getInt("if_second"));
            consumerPurchaseRates.setRegisterTime(resultSet.getString("register_time"));
            consumerPurchaseRates = JSON.parseObject(consumerPurchaseRates.toJson(), ConsumerPurchaseRates.class);
            list.add(consumerPurchaseRates);
        }
        return list;
    }

    private ConsumerPurchaseRates fillValue(ConsumerPurchaseRates consumerPurchaseRates,ResultSet resultSet) throws SQLException {
        consumerPurchaseRates.setRateOf1stWithin0(resultSet.getDouble(consumerPurchaseRates.getKeyType()));
        return consumerPurchaseRates;
    }

    @Override
    public BaseResponse<PageModel<List<ConsumerPurchaseRates>>> queryList(SysUserDTO dto) {
        List<ConsumerPurchaseRates> list = null;
        int status = 0;
        String errorMsg = "success";
        try {
            list = QueryMerchantRates(JSONObject.toJavaObject(dto.getQueryCondition(), ConsumerPurchaseRates.class));
        } catch (Exception e) {
            errorMsg = e.getMessage();
            e.printStackTrace();
            status = -1;
        }
        PageModel<List<ConsumerPurchaseRates>> pageModel = PageModel.<List<ConsumerPurchaseRates>>builder().data(list).pageIndex(dto.getPageIndex()).pageSize(list.size()).build();
        return new BaseResponse<PageModel<List<ConsumerPurchaseRates>>>(pageModel,status,errorMsg);
    }
}
