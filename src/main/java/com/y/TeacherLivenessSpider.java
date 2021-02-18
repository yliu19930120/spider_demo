package com.y;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TeacherLivenessSpider implements Spider{

//    private static final String URL = "https://www.icourse163.org/learn/SEU-1207045808?tid=1207406205#/learn/forumindex";

    private static Logger log = LoggerFactory.getLogger(TeacherLivenessSpider.class);

    //讨论区发帖地址
    private static final String URL = "https://www.icourse163.org/dwr/call/plaincall/PostBean.getAllPostsPagination.dwr";

    //回帖地址
    private static final String REPLY_URL = "https://www.icourse163.org/dwr/call/plaincall/PostBean.getPaginationReplys.dwr";
    //老师角色的关键字
    private static final String LECTOR_KEY = "lector";
    //助教角色的关键字
    private static final String ASSISTANT_KEY = "assistant";

    @Override
    public void run() {

        String params = "callCount=1\n" +
                "scriptSessionId=${scriptSessionId}190\n" +
                "httpSessionId=7fcf567154ee459593e4b3edc7863880\n" +
                "c0-scriptName=PostBean\n" +
                "c0-methodName=getAllPostsPagination\n" +
                "c0-id=0\n" +
                "c0-param0=number:1002458005\n" +
                "c0-param1=string:\n" +
                "c0-param2=number:1\n" +
                "c0-param3=string:4\n" +
                "c0-param4=number:20\n" +
                "c0-param5=boolean:false\n" +
                "c0-param6=null:null\n" +
                "batchId=1613641114939";

        try {
            String html = ReqUtils.post(params,URL);
            Dwr dwr = new Dwr(StringEscapeUtils.unescapeJava(html));
            JSONObject datas = dwr.getDatas();

            //分页列表储存的变量名
            String refeArrayKey = dwr.getRefeArrayKey();
            JSONArray jsonArray = datas.getJSONArray(refeArrayKey);

            List<Posts> postsList = new ArrayList<>();

            for(Object key:jsonArray){
                JSONObject jsonObject = datas.getJSONObject(key.toString());
                String id = jsonObject.getString("id");
                String title = jsonObject.getString("title");

                Posts posts = new Posts(id,title);

                String posterRefe = jsonObject.getString("poster");
                String replyCountStr = jsonObject.getString("countReply");
                Long countReply = replyCountStr==null?null:Long.parseLong(replyCountStr);

                String posterRole = null;
                if(posterRefe!=null && !"null".equals(posterRefe)){
                    JSONObject poster = datas.getJSONObject(posterRefe);
                    String rolesKey = poster.getString("roles");
                    if(rolesKey!=null && !"null".equals(rolesKey)){
                        JSONArray roles = datas.getJSONArray(rolesKey);
                        posterRole = roles.stream().map(Object::toString).findFirst().orElse(null);
                    }
                }

                posts.setPosterRoles(posterRole);
                posts.setReplyers(countReply);

                log.info("查看帖子 \"{}\",帖子id ={}",title,id);

                posts = getReplys(posts);

                postsList.add(posts);
            }

            long lectorCount = postsList.stream().filter(t -> LECTOR_KEY.equals(t.getPosterRoles())).count();
            long assistantCount= postsList.stream().filter(t -> ASSISTANT_KEY.equals(t.getPosterRoles())).count();
            long lectorRepCount = postsList.stream().filter(t -> t.getLectorReplyers()!=null)
                    .filter(t -> t.getLectorReplyers()>0).count();
            long assistantRepCount = postsList.stream().filter(t -> t.getAssistantReplyers()!=null)
                    .filter(t -> t.getAssistantReplyers()>0).count();

            Double a = Double.valueOf(postsList.size());
            Double b = Double.valueOf(lectorCount);
            Double c = Double.valueOf(assistantCount);
            Double d = Double.valueOf(lectorRepCount);
            Double e = Double.valueOf(assistantRepCount);

            Double tLiveness = d/(a-b-c)+0.5*e/(a-b-c);

            log.info("帖子总数={},老师发帖数量={},助教发帖数量={},老师参与回帖数量={},助教参与回帖数量={}，教师活跃度= {}",
                    postsList.size(),lectorCount,assistantCount,lectorRepCount,assistantRepCount,tLiveness);


            long count = postsList.stream().filter(t -> LECTOR_KEY.equals(t.getPosterRoles()) || ASSISTANT_KEY.equals(t.getPosterRoles()))
                    .map(Posts::getReplyers).count();

            Double sLiveness = Double.valueOf(count)/(a+b);

            log.info("老师发帖和助教发帖的总回帖数 = {},学生活跃度={}",count,sLiveness);


        } catch (IOException e) {
            log.error("io 异常",e);
        }
    }


    //查看回帖
    public Posts getReplys(Posts posts) throws IOException {

        String params = String.format("callCount=1\n" +
                "scriptSessionId=${scriptSessionId}190\n" +
                "httpSessionId=7fcf567154ee459593e4b3edc7863880\n" +
                "c0-scriptName=PostBean\n" +
                "c0-methodName=getPaginationReplys\n" +
                "c0-id=0\n" +
                "c0-param0=number:%s\n" +
                "c0-param1=number:2\n" +
                "c0-param2=number:1\n" +
                "batchId=1613641553584",posts.getId());

        String html = ReqUtils.post(params,REPLY_URL);

        Dwr dwr = new Dwr(StringEscapeUtils.unescapeJava(html));
        JSONObject datas = dwr.getDatas();

        //分页列表储存的变量名
        String refeArrayKey = dwr.getRefeArrayKey();
        JSONArray jsonArray = datas.getJSONArray(refeArrayKey);

        List<String> replyRoles = jsonArray.stream().flatMap(s->{
            JSONObject jsonObject = datas.getJSONObject(s.toString());
            String replyerKey = jsonObject.getString("replyer");

            if(replyerKey!=null && !"null".equals(replyerKey)){
                JSONObject replyer = datas.getJSONObject(replyerKey);
                String rolesKey = replyer.getString("roles");
                if(rolesKey!=null && !"null".equals(rolesKey)){
                    JSONArray roles = datas.getJSONArray(rolesKey);
                    return roles.stream().map(Object::toString);
                }
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        long lectorCount = replyRoles.stream().filter(t -> LECTOR_KEY.equals(t)).count();
        long  assistantCount= replyRoles.stream().filter(t -> ASSISTANT_KEY.equals(t)).count();
        log.info("回帖总数 ={} ,老师回帖数量={},助教回帖数量={}",jsonArray.size(),lectorCount,assistantCount);

        posts.setLectorReplyers(lectorCount);
        posts.setAssistantReplyers(assistantCount);
        return posts;
    }
}