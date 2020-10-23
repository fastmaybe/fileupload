package com.example.fileupload.utils;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 远程shell执行器
 *
 * @author huangchong
 * @date 2020/5/18 16:38
 */
public class RemoteShellExecutor {

    public static final Logger logger = LoggerFactory.getLogger(RemoteShellExecutor.class);

    private Connection conn;
    private String ip;
    private String username;
    private String password;
    private static final int TIME_OUT = 0;// 表示不超时

    /**
     * 构造函数
     *
     * @param ip       远程ip
     * @param username 远程机器用户名
     * @param password 远程机器密码
     */
    public RemoteShellExecutor(String ip, String username, String password) {
        this.ip = ip;
        this.username = username;
        this.password = password;
    }


    /**
     * 登录
     *
     * @return
     * @throws IOException
     */
    private boolean login() throws IOException {
        logger.info("远程shell执行器login()");
        conn = new Connection(ip);
        conn.connect();
        return conn.authenticateWithPassword(username, password);
    }

    /**
     * 执行脚本
     *
     * @param shell
     * @return
     * @throws Exception
     */
    public int exec(String shell) throws Exception {
        int ret = -1;
        try {
            if (login()) {
                logger.info("登录远程机器成功，开始执行脚本");
                Session session = conn.openSession();
                session.execCommand(shell);
                session.waitForCondition(ChannelCondition.EXIT_STATUS, TIME_OUT);
                ret = session.getExitStatus();
                logger.info("登录远程机器执行，返回:{}",ret);
            } else {
                logger.error("登录远程机器失败" + ip);
                throw new Exception("登录远程机器失败" + ip); // 自定义异常类 实现略
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
        return ret;
    }

}
