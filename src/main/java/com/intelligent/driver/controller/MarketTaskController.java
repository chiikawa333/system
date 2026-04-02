package com.intelligent.driver.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.intelligent.driver.entity.Admin;
import com.intelligent.driver.entity.MarketTask;
import com.intelligent.driver.mapper.AdminMapper;
import com.intelligent.driver.mapper.MarketTaskMapper;
import com.intelligent.driver.responce.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Controller
@RequestMapping("/market")
public class MarketTaskController {

    @Autowired
    private MarketTaskMapper marketTaskMapper;
    
    @Autowired
    private AdminMapper adminMapper;

    @GetMapping("/page")
    public String toPage() {
        return "redirect:/task.html";
    }

    @ResponseBody
    @GetMapping("/tasks")
    public R<List<MarketTask>> getTasks(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) String targetRole) {
        
        PageHelper.startPage(page, limit);
        LambdaQueryWrapper<MarketTask> wrapper = new LambdaQueryWrapper<>();
        if (targetRole != null && !targetRole.isEmpty()) {
            wrapper.eq(MarketTask::getTargetRole, targetRole);
        }
        wrapper.orderByDesc(MarketTask::getCreateTime);
        List<MarketTask> tasks = marketTaskMapper.selectList(wrapper);
        
        return R.data(tasks);
    }

    @ResponseBody
    @PostMapping("/task/publish")
    public R<MarketTask> publishTask(@RequestBody MarketTask task) {
        try {
            Object loginId = StpUtil.getLoginIdDefaultNull();
            if (loginId != null) {
                int publisherId = ((Number) loginId).intValue();
                task.setPublisherId(publisherId);
                
                Admin admin = adminMapper.selectById(publisherId);
                if (admin != null) {
                    task.setPublisherName(admin.getName());
                }
            } else {
                task.setPublisherId(11);
                task.setPublisherName("市场经理");
            }
        } catch (Exception e) {
            task.setPublisherId(11);
            task.setPublisherName("市场经理");
        }
        
        task.setStatus("pending");
        task.setCreateTime(LocalDateTime.now());
        if (task.getPriority() == null) {
            task.setPriority(1);
        }
        
        marketTaskMapper.insert(task);
        
        WebSocketServer.sendAll(task.getTargetRole(), task);
        
        return R.data(task);
    }

    @ResponseBody
    @PostMapping("/task/publish/batch")
    public R<List<MarketTask>> publishBatch(@RequestBody Map<String, Object> params) {
        MarketTask taskTemplate = (MarketTask) params.get("task");
        List<String> targetRoles = (List<String>) params.get("targetRoles");
        
        if (targetRoles == null || targetRoles.isEmpty()) {
            return R.fail("至少选择一个目标角色");
        }
        
        List<MarketTask> publishedTasks = new ArrayList<>();
        
        for (String role : targetRoles) {
            MarketTask task = new MarketTask();
            task.setTitle(taskTemplate.getTitle());
            task.setContent(taskTemplate.getContent());
            task.setPriority(taskTemplate.getPriority());
            task.setDeadline(taskTemplate.getDeadline());
            task.setTargetRole(role);
            
            try {
                Object loginId = StpUtil.getLoginIdDefaultNull();
                if (loginId != null) {
                    int publisherId = ((Number) loginId).intValue();
                    task.setPublisherId(publisherId);
                    
                    Admin admin = adminMapper.selectById(publisherId);
                    if (admin != null) {
                        task.setPublisherName(admin.getName());
                    }
                } else {
                    task.setPublisherId(11);
                    task.setPublisherName("市场经理");
                }
            } catch (Exception e) {
                task.setPublisherId(11);
                task.setPublisherName("市场经理");
            }
            
            task.setStatus("pending");
            task.setCreateTime(LocalDateTime.now());
            if (task.getPriority() == null) {
                task.setPriority(1);
            }
            
            marketTaskMapper.insert(task);
            publishedTasks.add(task);
            
            WebSocketServer.sendAll(role, task);
        }
        
        return R.data(publishedTasks);
    }

    @ResponseBody
    @PutMapping("/task/{id}/status")
    public R<MarketTask> updateStatus(@PathVariable Integer id, @RequestParam String status) {
        MarketTask task = marketTaskMapper.selectById(id);
        if (task != null) {
            task.setStatus(status);
            marketTaskMapper.updateById(task);
            return R.data(task);
        }
        return R.fail(500, "任务不存在");
    }

    @ResponseBody
    @PutMapping("/task/{id}/complete")
    public R<MarketTask> completeTask(@PathVariable Integer id, @RequestParam(required = false) String feedback) {
        MarketTask task = marketTaskMapper.selectById(id);
        if (task != null) {
            task.setStatus("completed");
            marketTaskMapper.updateById(task);
            
            WebSocketServer.sendAll("market_manager", task);
            
            return R.data(task);
        }
        return R.fail(500, "任务不存在");
    }

    @ResponseBody
    @GetMapping("/tasks/changes")
    public R<Map<String, Object>> getTaskChanges(
            @RequestParam String targetRole,
            @RequestParam(required = false) Long lastUpdate) {
        
        Map<String, Object> result = new HashMap<>();
        
        LambdaQueryWrapper<MarketTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MarketTask::getTargetRole, targetRole);
        wrapper.orderByDesc(MarketTask::getCreateTime);
        
        if (lastUpdate != null) {
            wrapper.gt(MarketTask::getCreateTime, new Date(lastUpdate));
        }
        
        List<MarketTask> tasks = marketTaskMapper.selectList(wrapper);
        result.put("tasks", tasks);
        result.put("hasChanges", !tasks.isEmpty());
        result.put("timestamp", System.currentTimeMillis());
        
        return R.data(result);
    }

    @ResponseBody
    @DeleteMapping("/task/{id}")
    public R<Void> deleteTask(@PathVariable Integer id) {
        marketTaskMapper.deleteById(id);
        return R.success();
    }
}

@Component
@ServerEndpoint("/ws/market/{role}")
class WebSocketServer {
    
    private static final Map<String, ConcurrentHashMap<Integer, Session>> ROLE_SESSIONS = new ConcurrentHashMap<>();
    
    @OnOpen
    public void onOpen(Session session, @PathParam("role") String role) {
        System.out.println("WebSocket 打开连接，角色：" + role + ", session: " + session.getId());
        ROLE_SESSIONS.computeIfAbsent(role, k -> new ConcurrentHashMap<>())
                    .put(session.hashCode(), session);
        System.out.println("当前角色 " + role + " 的在线人数：" + ROLE_SESSIONS.get(role).size());
    }

    @OnMessage
    public void onMessage(String message, @PathParam("role") String role) {
        System.out.println("收到消息 from " + role + ": " + message);
    }

    @OnClose
    public void onClose(Session session, @PathParam("role") String role) {
        System.out.println("WebSocket 关闭连接，角色：" + role);
        ConcurrentHashMap<Integer, Session> sessions = ROLE_SESSIONS.get(role);
        if (sessions != null) {
            sessions.remove(session.hashCode());
            System.out.println("剩余在线人数：" + sessions.size());
        }
    }

    public static void sendAll(String role, MarketTask task) {
        System.out.println("准备发送消息给角色：" + role);
        ConcurrentHashMap<Integer, Session> sessions = ROLE_SESSIONS.get(role);
        if (sessions != null && !sessions.isEmpty()) {
            StringBuilder json = new StringBuilder("{\"type\":\"new_task\",\"data\":{");
            json.append("\"id\":").append(task.getId());
            json.append(",\"title\":\"").append(task.getTitle().replace("\"", "\\\"")).append("\"");
            json.append(",\"content\":\"").append(task.getContent() != null ? task.getContent().replace("\"", "\\\"") : "").append("\"");
            json.append(",\"publisherName\":\"").append(task.getPublisherName() != null ? task.getPublisherName() : "市场经理").append("\"");
            json.append(",\"priority\":").append(task.getPriority());
            json.append(",\"createTime\":\"").append(task.getCreateTime()).append("\"");
            json.append("}}");
            
            int sentCount = 0;
            for (Session session : sessions.values()) {
                try {
                    if (session.isOpen()) {
                        session.getBasicRemote().sendText(json.toString());
                        sentCount++;
                        System.out.println("消息发送成功到 session: " + session.getId());
                    }
                } catch (IOException e) {
                    System.err.println("发送消息失败：" + e.getMessage());
                    e.printStackTrace();
                }
            }
            System.out.println("实际发送成功数量：" + sentCount + "/" + sessions.size());
        } else {
            System.out.println("警告：角色 " + role + " 没有在线用户，消息未发送");
        }
    }
}
