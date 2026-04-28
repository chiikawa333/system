package com.intelligent.driver.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.intelligent.driver.entity.Admin;
import com.intelligent.driver.entity.MarketTask;
import com.intelligent.driver.entity.TaskAttachment;
import com.intelligent.driver.mapper.AdminMapper;
import com.intelligent.driver.mapper.MarketTaskMapper;
import com.intelligent.driver.mapper.TaskAttachmentMapper;
import com.intelligent.driver.responce.R;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Tag(name = "市场任务管理")
@Controller
@RequestMapping("/market")
public class MarketTaskController {

    @Autowired
    private MarketTaskMapper marketTaskMapper;
    
    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private TaskAttachmentMapper taskAttachmentMapper;

    @GetMapping("/page")
    public String toPage() {
        return "redirect:/adminsystem/task.html";
    }

    @ResponseBody
    @GetMapping("/tasks")
    public R<List<MarketTask>> getTasks(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) String targetRole) {
        
        PageHelper.startPage(page, limit);
        LambdaQueryWrapper<MarketTask> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(MarketTask::getCreateTime);
        List<MarketTask> tasks = marketTaskMapper.selectList(wrapper);
        
        return R.data(tasks);
    }

    @ResponseBody
    @PostMapping("/task/publish")
    public R<MarketTask> publishTask(@RequestBody Map<String, Object> params) {
        try {
            MarketTask task = new MarketTask();
            task.setTitle((String) params.get("title"));
            task.setContent((String) params.get("content"));
            task.setPriority(params.get("priority") != null ? ((Number) params.get("priority")).intValue() : 1);
            task.setDeadline(params.get("deadline") != null ? (String) params.get("deadline") : null);
            task.setTargetRole((String) params.get("targetRole"));
            
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
            
            task.setStatus("pending");
            task.setCreateTime(LocalDateTime.now());
            
            marketTaskMapper.insert(task);
            
            WebSocketServer.sendAll(task.getTargetRole(), task);
            
            return R.data(task);
        } catch (Exception e) {
            return R.fail(500, "发布失败：" + e.getMessage());
        }
    }

    @ResponseBody
    @PostMapping("/task/publish/batch")
    public R<List<MarketTask>> publishBatch(@RequestBody Map<String, Object> params) {
        try {
            Map<String, Object> taskTemplate = (Map<String, Object>) params.get("task");
            List<String> targetRoles = (List<String>) params.get("targetRoles");
            
            if (targetRoles == null || targetRoles.isEmpty()) {
                return R.fail("至少选择一个目标角色");
            }
            
            List<MarketTask> publishedTasks = new ArrayList<>();
            
            for (String role : targetRoles) {
                MarketTask task = new MarketTask();
                task.setTitle((String) taskTemplate.get("title"));
                task.setContent((String) taskTemplate.get("content"));
                task.setPriority(taskTemplate.get("priority") != null ? ((Number) taskTemplate.get("priority")).intValue() : 1);
                task.setDeadline(taskTemplate.get("deadline") != null ? (String) taskTemplate.get("deadline") : null);
                task.setTargetRole(role);
                
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
                
                task.setStatus("pending");
                task.setCreateTime(LocalDateTime.now());
                
                marketTaskMapper.insert(task);
                publishedTasks.add(task);
                
                WebSocketServer.sendAll(role, task);
            }
            
            return R.data(publishedTasks);
        } catch (Exception e) {
            return R.fail(500, "发布失败：" + e.getMessage());
        }
    }

    @ResponseBody
    @PostMapping("/task/publish/with-file")
    public R<List<MarketTask>> publishTaskWithFile(
            @RequestPart("task") String taskJson,
            @RequestPart("targetRoles") String rolesJson,
            @RequestPart("file") MultipartFile file) {
    
        try {
            ObjectMapper mapper = new ObjectMapper();
            MarketTask taskTemplate = mapper.readValue(taskJson, MarketTask.class);
            List<String> targetRoles = mapper.readValue(rolesJson, 
                mapper.getTypeFactory().constructCollectionType(List.class, String.class));
            
            if (targetRoles == null || targetRoles.isEmpty()) {
                return R.fail("至少选择一个目标角色");
            }
            
            if (!file.isEmpty() && !file.getOriginalFilename().toLowerCase().endsWith(".txt")) {
                return R.fail("只支持 TXT 格式文件");
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
                
                if (!file.isEmpty()) {
                    String uploadDir = System.getProperty("user.dir") + "/uploads/tasks/" + task.getId() + "/";
                    Path dirPath = Paths.get(uploadDir);
                    if (!Files.exists(dirPath)) {
                        Files.createDirectories(dirPath);
                    }
                    
                    String newFileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                    Path filePath = dirPath.resolve(newFileName);
                    file.transferTo(filePath);
                    
                    TaskAttachment attachment = new TaskAttachment();
                    attachment.setTaskId(task.getId());
                    attachment.setMessageType("file");
                    attachment.setFileName(file.getOriginalFilename());
                    attachment.setFilePath("uploads/tasks/" + task.getId() + "/" + newFileName);
                    attachment.setFileSize(file.getSize());
                    attachment.setSenderId(task.getPublisherId());
                    attachment.setSenderName(task.getPublisherName());
                    attachment.setSenderRole("market_manager");
                    attachment.setCreateTime(LocalDateTime.now());
                    
                    taskAttachmentMapper.insert(attachment);
                }
                
                WebSocketServer.sendAll(role, task);
            }
            
            return R.data(publishedTasks);
        } catch (Exception e) {
            return R.fail(500, "发布失败：" + e.getMessage());
        }
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

    @ResponseBody
    @PostMapping("/task/{id}/message")
    public R<TaskAttachment> sendMessage(
            @PathVariable Integer id,
            @RequestParam String content,
            @RequestParam(defaultValue = "text") String type,
            @RequestParam(required = false) String visibleRoles) {
    
        try {
            TaskAttachment attachment = new TaskAttachment();
            attachment.setTaskId(id);
            attachment.setMessageType(type);
            attachment.setMessageContent(content);
            
            Object loginId = StpUtil.getLoginIdDefaultNull();
            if (loginId != null) {
                int publisherId = ((Number) loginId).intValue();
                attachment.setSenderId(publisherId);
                
                Admin admin = adminMapper.selectById(publisherId);
                if (admin != null) {
                    attachment.setSenderName(admin.getName());
                    attachment.setSenderRole(admin.getUsername());
                }
            } else {
                attachment.setSenderId(11);
                attachment.setSenderName("工程师");
                attachment.setSenderRole("engineer");
            }
            attachment.setCreateTime(LocalDateTime.now());
            
            taskAttachmentMapper.insert(attachment);
            
            // 根据可见角色推送消息
            if (visibleRoles != null && !visibleRoles.isEmpty()) {
                String[] roles = visibleRoles.split(",");
                for (String role : roles) {
                    WebSocketServer.sendAttachmentMessage(role.trim(), attachment);
                }
            } else {
                // 默认推送给所有人
                WebSocketServer.sendAttachmentMessageToAll(id, attachment);
            }
            
            return R.data(attachment);
        } catch (Exception e) {
            return R.fail(500, "发送失败：" + e.getMessage());
        }
    }

    @ResponseBody
    @PostMapping("/task/{id}/upload")
    public R<TaskAttachment> uploadFile(
            @PathVariable Integer id,
            @RequestParam("file") MultipartFile file) {
    
        try {
            if (file.isEmpty()) {
                return R.fail("文件不能为空");
            }
            
            String originalFilename = file.getOriginalFilename();
            if (!originalFilename.toLowerCase().endsWith(".txt")) {
                return R.fail("只支持 TXT 格式文件");
            }
            
            String uploadDir = System.getProperty("user.dir") + "/uploads/tasks/" + id + "/";
            Path dirPath = Paths.get(uploadDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            
            String newFileName = System.currentTimeMillis() + "_" + originalFilename;
            Path filePath = dirPath.resolve(newFileName);
            file.transferTo(filePath);
            
            TaskAttachment attachment = new TaskAttachment();
            attachment.setTaskId(id);
            attachment.setMessageType("file");
            attachment.setFileName(originalFilename);
            attachment.setFilePath("/uploads/tasks/" + id + "/" + newFileName);
            attachment.setFileSize(file.getSize());
            
            Object loginId = StpUtil.getLoginIdDefaultNull();
            if (loginId != null) {
                int publisherId = ((Number) loginId).intValue();
                attachment.setSenderId(publisherId);
                
                Admin admin = adminMapper.selectById(publisherId);
                if (admin != null) {
                    attachment.setSenderName(admin.getName());
                    attachment.setSenderRole(admin.getUsername());
                }
            } else {
                attachment.setSenderId(11);
                attachment.setSenderName("工程师");
                attachment.setSenderRole("engineer");
            }
            attachment.setCreateTime(LocalDateTime.now());
            
            taskAttachmentMapper.insert(attachment);
            
            WebSocketServer.sendAttachmentMessageToAll(id, attachment);
            
            return R.data(attachment);
        } catch (Exception e) {
            return R.fail(500, "上传失败：" + e.getMessage());
        }
    }

    @ResponseBody
    @GetMapping("/task/{id}/messages")
    public R<List<TaskAttachment>> getMessages(@PathVariable Integer id) {
        try {
            LambdaQueryWrapper<TaskAttachment> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TaskAttachment::getTaskId, id);
            wrapper.orderByAsc(TaskAttachment::getCreateTime);
            List<TaskAttachment> messages = taskAttachmentMapper.selectList(wrapper);
            return R.data(messages);
        } catch (Exception e) {
            return R.fail(500, "获取消息失败：" + e.getMessage());
        }
    }

    @ResponseBody
    @GetMapping("/task/{id}/attachments")
    public R<List<TaskAttachment>> getAttachments(@PathVariable Integer id) {
        try {
            LambdaQueryWrapper<TaskAttachment> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TaskAttachment::getTaskId, id);
            wrapper.orderByAsc(TaskAttachment::getCreateTime);
            List<TaskAttachment> attachments = taskAttachmentMapper.selectList(wrapper);
            return R.data(attachments);
        } catch (Exception e) {
            return R.fail(500, "获取附件失败：" + e.getMessage());
        }
    }

    @ResponseBody
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam String path, @RequestParam(required = false) String download) throws IOException {
        Path filePath = Paths.get("/home/ubuntu/app/" + path);
        Resource resource = new UrlResource(filePath.toUri());
        
        if (!resource.exists()) {
            throw new RuntimeException("文件不存在");
        }
        
        String fileName = download != null ? URLDecoder.decode(download, "UTF-8") : resource.getFilename();
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + new String(fileName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1) + "\"")
                .body(resource);
    }
}

@Component
@ServerEndpoint("/ws/market/{role}")
class WebSocketServer {
    
    private static final Map<String, ConcurrentHashMap<Integer, Session>> ROLE_SESSIONS = new ConcurrentHashMap<>();
    private static TaskAttachmentMapper staticTaskAttachmentMapper;
    
    @Autowired
    public void setTaskAttachmentMapper(TaskAttachmentMapper mapper) {
        WebSocketServer.staticTaskAttachmentMapper = mapper;
    }
    
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
    
    public static void sendAttachmentMessage(String targetRole, TaskAttachment attachment) {
        System.out.println("推送任务消息到角色：" + targetRole);
        
        StringBuilder json = new StringBuilder("{\"type\":\"new_message\",\"data\":{");
        json.append("\"taskId\":").append(attachment.getTaskId());
        json.append(",\"senderName\":\"").append(attachment.getSenderName()).append("\"");
        json.append(",\"messageType\":\"").append(attachment.getMessageType()).append("\"");
        
        if ("text".equals(attachment.getMessageType())) {
            json.append(",\"content\":\"").append(attachment.getMessageContent() != null ? attachment.getMessageContent().replace("\"", "\\\"") : "").append("\"");
        } else {
            json.append(",\"fileName\":\"").append(attachment.getFileName() != null ? attachment.getFileName().replace("\"", "\\\"") : "").append("\"");
            json.append(",\"fileSize\":").append(attachment.getFileSize() != null ? attachment.getFileSize() : 0);
        }
        
        json.append(",\"createTime\":\"").append(attachment.getCreateTime()).append("\"");
        json.append("}}");
        
        ConcurrentHashMap<Integer, Session> sessions = ROLE_SESSIONS.get(targetRole);
        if (sessions != null) {
            for (Session session : sessions.values()) {
                try {
                    if (session.isOpen()) {
                        session.getBasicRemote().sendText(json.toString());
                    }
                } catch (IOException e) {
                    System.err.println("推送消息失败：" + e.getMessage());
                }
            }
        }
    }
    
    public static void sendAttachmentMessageToAll(Integer taskId, TaskAttachment attachment) {
        System.out.println("推送任务消息到所有角色");
        
        StringBuilder json = new StringBuilder("{\"type\":\"new_message\",\"data\":{");
        json.append("\"taskId\":").append(taskId);
        json.append(",\"senderName\":\"").append(attachment.getSenderName()).append("\"");
        json.append(",\"messageType\":\"").append(attachment.getMessageType()).append("\"");
        
        if ("text".equals(attachment.getMessageType())) {
            json.append(",\"content\":\"").append(attachment.getMessageContent() != null ? attachment.getMessageContent().replace("\"", "\\\"") : "").append("\"");
        } else {
            json.append(",\"fileName\":\"").append(attachment.getFileName() != null ? attachment.getFileName().replace("\"", "\\\"") : "").append("\"");
            json.append(",\"fileSize\":").append(attachment.getFileSize() != null ? attachment.getFileSize() : 0);
        }
        
        json.append(",\"createTime\":\"").append(attachment.getCreateTime()).append("\"");
        json.append("}}");
        
        // 推送给所有角色
        for (ConcurrentHashMap<Integer, Session> sessions : ROLE_SESSIONS.values()) {
            if (sessions != null) {
                for (Session session : sessions.values()) {
                    try {
                        if (session.isOpen()) {
                            session.getBasicRemote().sendText(json.toString());
                        }
                    } catch (IOException e) {
                        System.err.println("推送消息失败：" + e.getMessage());
                    }
                }
            }
        }
    }
}
