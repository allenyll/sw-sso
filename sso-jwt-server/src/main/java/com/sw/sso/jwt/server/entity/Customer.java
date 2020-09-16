package com.sw.sso.jwt.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * <p>
 * 会员表
 * </p>
 *
 * @author yu.leilei
 * @since 2018-10-22
 */
@Data
@ToString
@TableName("snu_customer")
public class Customer{

    private static final long serialVersionUID = 1L;

    /**
     * 会员ID
     */
	@TableId(type = IdType.ASSIGN_ID)
	private String id;
	/**
	 * 会员名称
	 */
	private String customerName;
	/**
	 * 会员账号
	 */
	private String customerAccount;
	/**
	 * 昵称
	 */
	private String nickName;
	/**
	 * 密码
	 */
	private String password;
	/**
	 * 密码盐
	 */
	private String salt;
	/**
	 * 状态
	 */
	private String status;
	/**
	 * 手机号
	 */
	private String phone;
	/**
	 * 邮箱
	 */
	private String email;
	/**
	 * 性别
	 */
	private String gender;
	/**
	 * 头像
	 */
	private String avatarUrl;
	/**
	 * 国家
	 */
	private String country;
	/**
	 * 省份
	 */
	private String province;
	/**
	 * 城市
	 */
	private String city;
	/**
	 * 地址
	 */
	private String address;
	/**
	 * 微信openid
	 */
	private String openid;

	/**
	 * 是否删除
	 */
	private Integer isDelete;
	/**
	 * 创建人
	 */
	private Long addUser;
	/**
	 * 创建时间
	 */
	private String addTime;
	/**
	 * 更新人
	 */
	private Long updateUser;
	/**
	 * 更新时间
	 */
	private String updateTime;

}
