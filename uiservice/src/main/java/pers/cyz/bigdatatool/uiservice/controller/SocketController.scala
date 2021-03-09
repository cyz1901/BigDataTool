//package pers.cyz.bigdatatool.uiservice.controller
//
//import org.springframework.stereotype.Controller
//import org.springframework.web.bind.annotation.GetMapping
//import org.springframework.web.bind.annotation.PathVariable
//import org.springframework.web.bind.annotation.RequestMapping
//import org.springframework.web.bind.annotation.ResponseBody
//import org.springframework.web.servlet.ModelAndView
//
//@Controller
//@RequestMapping(Array("/websocket/download/")) class SocketController { //页面请求
//  @GetMapping(Array("/websocket/download/")) def socket(@PathVariable cid: String): ModelAndView = {
//    println("hhh")
//    val mav = new ModelAndView("/socket")
//    mav.addObject("cid", cid)
//    mav
//  }
//
//}