package com.y;

import com.alibaba.fastjson.JSONObject;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

/**
 * 满意度爬虫
 */
public class DegreeSpider implements Spider {

    private static Logger log = LoggerFactory.getLogger(DegreeSpider.class);

    @Override
    public void run() {
        //课程id
        String courseId = "1002335004";

        Evaluate evaluate = getEvaluate(courseId);

        log.info("评价人数 = {}",evaluate.getEvaluateCount());
    }

    public Evaluate getEvaluate(String courseId){
        String csrfKey = UUID.randomUUID().toString().replace("-","");
        String url = "https://www.icourse163.org/web/j/mocCourseV2RpcBean.getEvaluateAvgAndCount.rpc?csrfKey=%s";
        url = String.format(url,csrfKey);
        String cookie = String.format("NTESSTUDYSI=%s; EDUWEBDEVICE=ff38b8f5e844450a93619434db095319; Hm_lvt_77dc9a9d49448cf5e629e5bebaa5500b=1613694505; Hm_lpvt_77dc9a9d49448cf5e629e5bebaa5500b=1613694505; WM_NI=HMDVZwEB2ocJs4zw%%2F5eksvlAna%%2F3M667sIKlTymrVpR0mh%%2F3T9nrO%%2F6wxBkEf%%2BIGN9T9gqofj7YV02AK3ENXvkmMKaYFsBGWu6eU20%%2B0tF3ixo2Auri95iZrKKLzLWv%%2FNm4%%3D; WM_NIKE=9ca17ae2e6ffcda170e2e6eea4b27ee99088cccc49908e8ba3c54b979e8aabae6bfbacf99aef5e85b7fca7f42af0fea7c3b92a8c938cb7d97d879efe90e242b1ecb698ae3fa7b5bd97ef4baebfe1d4d03ef6e9bdaaca6898a7a393d57c92f0a389ae3998e7a293ce25f6a9b8a3ea4fa6f587a8cd3398e7a992ee6eaae997b7b33a8395fda6cd54b6b5a3aeca6b9b9abda3c65db7eaabb6b13ce9f0bca7b263bcb1fea2f87aabe7fb8ad97a8995849bcf5db6f1828eb737e2a3; WM_TID=ZPnWvW5YbrBAUQUFQVJuf5s7mlVCy8F6; __yadk_uid=pVKri4AysMzL1F16XxdeeGG6M2DCc95k"
                ,csrfKey);
        Headers headers = Headers.of("Cookie",cookie);
        RequestBody requestBody = new FormBody.Builder().add("courseId",courseId).build();

        try {
            String post = ReqUtils.post(headers, requestBody, url);
            JSONObject jsonObject = JSONObject.parseObject(post);
            return jsonObject.getObject("result", Evaluate.class);
        } catch (IOException e) {
            log.error("courseId = {} 抓取错误");
            log.error("异常",e);
            return null;
        }
    }
}
