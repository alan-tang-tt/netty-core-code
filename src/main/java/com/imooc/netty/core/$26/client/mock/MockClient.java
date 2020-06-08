package com.imooc.netty.core.$26.client.mock;

import com.imooc.netty.core.$26.common.domain.Player;
import com.imooc.netty.core.$26.common.domain.Table;
import com.imooc.netty.core.$26.common.msg.*;
import com.imooc.netty.core.$26.common.protocol.MahjongMsg;
import com.imooc.netty.core.$26.util.MsgUtils;
import io.netty.channel.Channel;
import io.netty.channel.DefaultEventLoopGroup;

import java.util.Scanner;

public class MockClient {

    private static DefaultEventLoopGroup executorGroup;
    private static Channel channel;
    private static Player player;
    private static Scanner scanner = new Scanner(System.in);

    public static void setExecutorGroup(DefaultEventLoopGroup executorGroup) {
        MockClient.executorGroup = executorGroup;
    }

    public static void setChannel(Channel channel) {
        MockClient.channel = channel;
    }

    public static void start() {
        login();
    }

    public static void login() {
        oneOperationExecute("请选择您要进行的操作：1. 登录",
                "请输入用户名和密码，以空格分隔：",
                (line) -> line.contains(" "),
                (line) -> {
                    String[] strings = line.split(" ");
                    LoginRequest loginRequest = new LoginRequest();
                    loginRequest.setUsername(strings[0]);
                    loginRequest.setPassword(strings[1]);
                    return loginRequest;
                });
    }

    public static void loginResponse(LoginResponse response) {
        if (response.isResult()) {
            Player player = response.getPlayer();
            System.out.println("恭喜您，登录成功，您的信息如下：\n" + player);
            afterLoginResponse();
        } else {
            System.out.println("登录失败：" + response.getMsg());
            login();
        }
    }

    private static void afterLoginResponse() {
        twoOperationExecute("请选择您要进行的操作：1. 创建房间，2. 加入房间",
                "正在为您创建房间，请稍候...",
                null,
                (line) -> {
                    CreateTableRequest request = new CreateTableRequest();
                    request.setBaseScore(5);
                    request.setPlayerNum(4);
                    return request;
                },
                "请输入您要加入的房间号：",
                (line) -> {
                    try {
                        Long.parseLong(line);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                },
                (line) -> {
                    EnterTableRequest request = new EnterTableRequest();
                    request.setTableId(Long.parseLong(line));
                    return request;
                }
        );
    }

    public static void createTableResponse(CreateTableResponse response) {
        Table table = response.getTable();
        System.out.println("恭喜您，创建房间成功，房间信息如下：\n" + table);
        System.out.println("请稍等，正在为您匹配玩家...");
    }

    public static void enterTableResponse(EnterTableResponse response) {
        if (response.isResult()) {
            System.out.println("恭喜您，加入房间成功，房间信息如下：\n" + response.getTable());
        } else {
            System.out.println("不好意思，加入房间失败：" + response.getMsg());
            afterLoginResponse();
        }
    }

    public static void tableNotification(TableNotification response) {
        Table table = response.getTable();
        if (table.getStatus() == Table.STATUS_WAITING) {
            System.out.println("有人进入房间，刷新房间信息如下：\n" + table);
        } else if (table.getStatus() == Table.STATUS_STARTING) {
            System.out.println("所有玩家已就绪，游戏即将开始~~");
        } else if (table.getStatus() == Table.STATUS_PLAYING) {
            if (table.getSequence() == 0) {
                System.out.println("发牌完毕，请等待庄家出牌~~");
            } else {
                System.out.println("刷新牌局信息：\n" + table);
            }
        } else {
            System.out.println("游戏已结束！");
            afterLoginResponse();
        }
    }

    private interface Conditin {
        boolean run(String line);
    }

    private interface MsgBuilder {
        MahjongMsg build(String line);
    }

    private static void oneOperationExecute(String tips, String tips1, Conditin condition, MsgBuilder msgBuilder) {
        executorGroup.execute(() -> {
            System.out.println(tips);
            boolean flag = false;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (!flag && "1".equals(line)) {
                    flag = true;
                    System.out.println(tips1);
                    if (condition == null) {
                        MahjongMsg msg = msgBuilder.build(line);
                        MsgUtils.send(channel, msg);
                        break;
                    }
                } else if (flag && condition.run(line)) {
                    MahjongMsg msg = msgBuilder.build(line);
                    MsgUtils.send(channel, msg);
                    break;
                } else {
                    System.out.println("错误的输入，请重新输入：");
                }
            }
        });
    }

    private static void twoOperationExecute(String tips, String tips1, Conditin condition1, MsgBuilder msgBuilder1, String tips2, Conditin condition2, MsgBuilder msgBuilder2) {
        executorGroup.execute(() -> {
            System.out.println(tips);
            boolean flag1 = false;
            boolean flag2 = false;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (!flag1 && !flag2 && "1".equals(line)) {
                    flag1 = true;
                    System.out.println(tips1);
                    if (condition1 == null) {
                        MahjongMsg msg = msgBuilder1.build(line);
                        MsgUtils.send(channel, msg);
                        break;
                    }
                } else if (!flag1 && !flag2 && "2".equals(line)) {
                    flag2 = true;
                    System.out.println(tips2);
                    if (condition2 == null) {
                        MahjongMsg msg = msgBuilder2.build(line);
                        MsgUtils.send(channel, msg);
                        break;
                    }
                } else if (flag1 && condition1.run(line)) {
                    MahjongMsg msg = msgBuilder1.build(line);
                    MsgUtils.send(channel, msg);
                    break;
                } else if (flag2 && condition2.run(line)) {
                    MahjongMsg msg = msgBuilder2.build(line);
                    MsgUtils.send(channel, msg);
                    break;
                } else {
                    System.out.println("错误的输入，请重新输入：");
                }
            }
        });
    }
}
