package com.sw.sso.jwt.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;


/**
 * <p>
 * 用户表
 * 设置用户权限，所以要放在公用模块
 * </p>
 *
 * @author yu.leilei
 * @since 2018-06-12
 */
@TableName("sys_user")
@Data
@ToString
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 用户主键
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private long id;
	/**
	 * 组织ID
	 */
	private long depotId;

	@TableField(exist = false)
	private String depotName;

	/**
	 * 名称
	 */
	private String userName;
	/**
	 * 账号
	 */
	private String account;
	/**
	 * 密码
	 */
	private String password;
	/**
	 * 密码盐
	 */
	private String salt;
	/**
	 * 会员状态
	 */
	private String status;

	/**
	 * 电话
	 */
	private String phone;
	/**
	 * 邮箱
	 */
	private String email;
	/**
	 * 性别
	 */
	private String sex;

	/**
	 * 头像
	 */
	private String picId;
	/**
	 * 地址
	 */
	private String address;
	/**
	 * 省份
	 */
	private String province;
	/**
	 * 城市
	 */
	private String city;
	/**
	 * 区划
	 */
	private String area;
	/**
	 * 最后更新密码时间
	 */
	private Date lastPasswordResetDate;

	/**
	 * 是否删除
	 */
	private Integer isDelete;
	/**
	 * 创建人
	 */
	private String addUser;
	/**
	 * 创建时间
	 */
	private String addTime;
	/**
	 * 更新人
	 */
	private String updateUser;
	/**
	 * 更新时间
	 */
	private String updateTime;

}



