/**
 * Author: wheat (github.com/wheat2018)
 * Date: 2020-11-25
 */

package com.example.todayBread.wheat;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

/**
 * 数据批发器。
 * 依赖于一个数据迭代器，可选依赖于一个数据包装器。当指定一个批发数量n时，DataOutlet向数据迭代器
 * 尝试迭代n次，并用数据包装器将每个返回值进行包装，最终返回不大于n的包装数据数组。此过程可以是异步的。
 */
public class DataOutlet{
    private final Iterator<Object> dataIterator;
    private final Function<Object, Object> dataPacker;

    /**
     * 构造函数。
     * @param dataIterator 数据迭代器。
     * @param dataPacker 数据包装器。
     */
    public DataOutlet(Iterator<Object> dataIterator, Function<Object, Object> dataPacker){
        this.dataIterator = dataIterator;
        this.dataPacker = dataPacker;
    }

    public boolean empty(){
        return !dataIterator.hasNext();
    }

    /**
     * 同步获取批量数据。
     * @param batchSize 批量大小。
     * @return 数据批。
     */
    public Object[] getBatch(int batchSize){
        ArrayList<Object> batch = new ArrayList<>(batchSize);
        while (dataIterator.hasNext() && batchSize > 0){
            --batchSize;
            batch.add(dataPacker.apply(dataIterator.next()));
        }
        return batch.toArray();
    }

    /**
     * 异步获取批量数据，完成时调用回调函数。
     * @param batchSize 批量大小。
     * @param callback 完成时回调。要求一个以Object[]为唯一参数的Lambda。
     */
    public void asyncGetBatch(int batchSize, Utils.Function<Object[]> callback){
        new Thread(() -> {
            callback.run(getBatch(batchSize));
        }).start();
    }
}

class IteratorConvert{
    static class JSONArrayIterator implements Iterator<Object>{
        private final JSONArray array;
        private int idx = 0;
        public JSONArrayIterator(JSONArray array){
            this.array = array;
        }

        @Override
        public boolean hasNext() {
            return array.length() > idx;
        }

        @Override
        public Object next() {
            try {
                Object obj = array.get(idx);
                ++idx;
                return obj;
            } catch (JSONException e) {
                throw new NoSuchElementException();
            }
        }
    }
}