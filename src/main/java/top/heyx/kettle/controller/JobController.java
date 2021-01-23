//package top.heyx.kettle.controller;
//
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//import top.heyx.kettle.handler.ETLJobHandler;
//
////import top.heyx.kettle.handler:q.KettleHandler;
//@Controller
//public class JobController {
////    @Autowired
////    KettleHandler kettleHandler;
//    @Autowired
//    ETLJobHandler kettleHandler;
//
//
//    @RequestMapping("/job")
//    @ResponseBody
//    public String runJob(String params) throws Exception {
//        String parameter = StringUtils.trim(params);
//        kettleHandler.kettleFile(parameter);
//        return "";
//    }
//
//    @RequestMapping("/repjob")
//    @ResponseBody
//    public String runRepJob(String params) throws Exception {
//        params="/cbjf/cbxx/5.0/,jobname=漯河市";
//        kettleHandler.kettleRepository(params);
//        return "成功";
//    }
//
//
//}
