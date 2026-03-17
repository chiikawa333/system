//package com.jiangxijiaoyuan.entity;
//
//import com.baomidou.mybatisplus.annotation.IdType;
//import com.baomidou.mybatisplus.annotation.TableField;
//import com.baomidou.mybatisplus.annotation.TableId;
//import com.baomidou.mybatisplus.annotation.TableName;
//import io.swagger.v3.oas.annotations.media.Schema;
//import lombok.Data;
//
//@Data
//@TableName("admins")
//@Schema(description = "管理员信息实体类")
//public class Admin {
//
//    @TableId(type = IdType.AUTO)
//    private Long id;
//
//    @Schema(description = "用户名")
//    private String username;
//
//
//    private String userpwd;
//
//    private String name;
//
//    private String sex;
//
//    private String tel;
//
//    private String headurl;
//
//    @TableField(exist = false)
//    private String captchaId;
//
//    @TableField(exist = false)
//    private String captchaCode;
//
//    @TableField(exist = false)
//    private String token;
//
//}
package com.jiangxijiaoyuan.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@TableName("admins")
@Schema(description = "管理员信息实体类")
public class Admin {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键 ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String userpwd;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "性别")
    private String sex;

    @Schema(description = "电话")
    private String tel;

    @Schema(description = "头像 URL")
    private String headurl;

    @TableField(exist = false)
    @Schema(description = "验证码 ID", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String captchaId;

    @TableField(exist = false)
    @Schema(description = "验证码", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String captchaCode;

    @TableField(exist = false)
    @Schema(description = "令牌", accessMode = Schema.AccessMode.READ_ONLY)
    private String token;

}




