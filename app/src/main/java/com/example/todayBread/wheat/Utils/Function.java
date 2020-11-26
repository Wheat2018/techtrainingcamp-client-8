package com.example.todayBread.wheat.Utils;


/**
 * 接受一个参数，无返回值的Lambda接口。
 * @param <Arg> 输入参数类型。
 */
public interface Function<Arg>{
    void run(Arg arg);
}
