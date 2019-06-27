package net.cofcool.chaos.server.demo.api;

import java.util.List;
import lombok.Data;

/**
 * 仅用来生成文档
 */
@Data
public class QueryMessage<T> {

    /**
     * 总行数
     */
    private long totalRow;
    /**
     * 数据
     */
    private List<T> list;
    /**
     * 是否第一页
     */
    private boolean firstPage;
    /**
     * 是否最后一页
     */
    private boolean lastPage;
    /**
     * 页码
     */
    private int pageNumber;
    /**
     * 每页数量
     */
    private int pageSize;
    /**
     * 总页数
     */
    private int totalPage;

}
