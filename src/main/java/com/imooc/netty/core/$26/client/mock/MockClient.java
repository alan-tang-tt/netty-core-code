package com.imooc.netty.core.$26.client.mock;

import com.imooc.netty.core.$26.common.domain.Player;
import com.imooc.netty.core.$26.common.domain.Table;
import com.imooc.netty.core.$26.common.msg.*;
import com.imooc.netty.core.$26.common.protocol.MahjongMsg;
import com.imooc.netty.core.$26.util.CardUtils;
import com.imooc.netty.core.$26.util.MsgUtils;
import com.imooc.netty.core.$26.util.OperationUtils;
import io.netty.channel.Channel;
import io.netty.channel.DefaultEventLoopGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class MockClient {

    private static DefaultEventLoopGroup executorGroup;
    private static Channel channel;
    private static Table table;
    private static Player player;
    private static Scanner scanner = new Scanner(System.in);
    private static DefaultEventLoopGroup asyncExecutorGroup = new DefaultEventLoopGroup(1);

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
        oneOperationExecute("\n请您登录，用户名和密码，以空格分隔：",
                (line) -> line.split(" ").length == 2,
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
            player = response.getPlayer();
            System.out.println("\n恭喜您，登录成功，您的信息如下：\n" + player);
            afterLoginResponse();
        } else {
            System.out.println("\n登录失败：" + response.getMsg());
            login();
        }
    }

    private static void afterLoginResponse() {
        twoOperationExecute("\n请选择您要进行的操作：1. 创建房间，2. 加入房间",
                "\n正在为您创建房间，请稍候...",
                null,
                (line) -> {
                    CreateTableRequest request = new CreateTableRequest();
                    request.setBaseScore(5);
                    request.setPlayerNum(2);
                    return request;
                },
                "\n请输入您要加入的房间号：",
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
        table = response.getTable();
        System.out.println("\n恭喜您，创建房间成功，房间信息如下：\n" + table);
        System.out.println("\n请稍等，正在为您匹配玩家...");
    }

    public static void enterTableResponse(EnterTableResponse response) {
        if (response.isResult()) {
            System.out.println("\n恭喜您，加入房间成功，房间信息如下：\n" + response.getTable());
        } else {
            System.out.println("\n不好意思，加入房间失败：" + response.getMsg());
            afterLoginResponse();
        }
    }

    public static void tableNotification(TableNotification notification) {
        table = notification.getTable();
        if (table.getStatus() == Table.STATUS_WAITING) {
            System.out.println("\n有人进入房间，刷新房间信息如下：\n" + table);
        } else if (table.getStatus() == Table.STATUS_STARTING) {
            System.out.println("\n所有玩家已就绪，游戏即将开始~~");
        } else if (table.getStatus() == Table.STATUS_PLAYING) {
            System.out.println("\n刷新牌局信息：");
            printCardsOf(table);
        } else {
            System.out.println("\n游戏已结束！");
            afterLoginResponse();
        }
    }

    public static void operationNotification(OperationNotification notification) {
        if (notification.getOperationPos() == player.getPos()) {
            if (notification.getOperation() == OperationUtils.OPERATION_CHU) {
                chu(notification);
            } else {
                // 其它操作有这么几种组合：
                // 碰 取消、杠 取消、胡 取消、碰 杠 取消、 碰 胡 取消、 杠 胡 取消、碰 杠 胡 取消
                System.out.println("\n请您操作：xxxxxxxxxx");

            }
        } else {
            if (notification.getOperation() == OperationUtils.OPERATION_CHU) {
                System.out.println("\n请等待 玩家" + (notification.getOperationPos() + 1) + " 出牌（倒计时 " + notification.getDelayTime() + "秒）");
            } else {
                System.out.println("\n请等待其他玩家操作（倒计时 " + notification.getDelayTime() + "秒）");
            }
        }
    }

    public static void operationResultNotification(OperationResultNotification notification) {
        if (notification.getOperationPos() == player.getPos()) {
            System.out.println("\n您 " + operation(notification.getOperation()) + " 了 " + formatCards(notification.getCards()));
        } else {
            System.out.println("\n玩家" + (notification.getOperationPos() + 1) + " " + operation(notification.getOperation()) + " 了 " + formatCards(notification.getCards()));
        }
    }

    public static void settleNotification(SettleNotification notification) {
        if (player.getPos() == notification.getWinnerPos()) {
            System.out.println("恭喜您，赢钱了赢钱了~~");
        }
        for (Player player : table.getPlayers()) {
            if (player.getPos() == notification.getWinnerPos()) {
                System.out.println("玩家" +(player.getPos()+1)+ ": " + notification.getBaseScore() * (table.getMaxPlayerNum()-1));
            } else {
                System.out.println("玩家" +(player.getPos()+1)+ ": " + notification.getBaseScore());
            }
        }
    }

    private static String operation(int operation) {
        if (operation == OperationUtils.OPERATION_CHU) {
            return "出";
        }
        if (operation == OperationUtils.OPERATION_PENG) {
            return "碰";
        }
        if (operation == OperationUtils.OPERATION_GANG) {
            return "杠";
        }
        if (operation == OperationUtils.OPERATION_HU) {
            return "胡";
        }
        return null;
    }

    private static void chu(OperationNotification notification) {
        oneOperationExecute("\n请您出牌（倒计时 " + notification.getDelayTime() + "秒）：", line -> {
            char[] chars = line.toCharArray();
            if (chars.length != 2) {
                return false;
            }
            if (chars[0] < '1' || chars[0] > '9') {
                return false;
            }
            if (chars[1] != '万' && chars[1] != '条' && chars[1] != '筒') {
                return false;
            }
            byte value = Byte.parseByte(String.valueOf(chars[0]));
            byte color = chars[1] == '万' ? CardUtils.WAN_MASK :
                    chars[1] == '条' ? CardUtils.TIAO_MASK : CardUtils.TONG_MASK;

            byte card = (byte) (value | color);
            for (byte playerCard : player.getCards()) {
                if (card == playerCard) {
                    return true;
                }
            }

            return false;
        }, line -> {
            char[] chars = line.toCharArray();
            byte value = Byte.parseByte(String.valueOf(chars[0]));
            byte color = chars[1] == '万' ? CardUtils.WAN_MASK :
                    chars[1] == '条' ? CardUtils.TIAO_MASK : CardUtils.TONG_MASK;

            byte card = (byte) (value | color);

            OperationRequest operationRequest = new OperationRequest();
            operationRequest.setOperation(OperationUtils.OPERATION_CHU);
            operationRequest.setOperationPos(player.getPos());
            operationRequest.setCards(new byte[]{card});
            operationRequest.setSequence(table.getSequence());
            return operationRequest;
        }, true);
    }

    private static void printCardsOf(Table table) {
        Player[] players = table.getPlayers();
        for (Player player : players) {
            if (player.getId() == MockClient.player.getId()) {
                MockClient.player = player;
                System.out.println("你的牌：" + formatCards(player.getCards()));
            } else {
                System.out.println("玩家" + (player.getPos() + 1) + "的牌：" + formatCards(player.getCards()));
            }
        }
    }

    private static String formatCards(byte[] cards) {
        Arrays.sort(cards);
        List<String> list = new ArrayList<>();
        for (byte card : cards) {
            if (card != 0) {
                list.add(toChinese(card));
            }
        }
        return list.toString();
    }

    private static String toChinese(byte card) {
        if (CardUtils.isWan(card)) {
            return CardUtils.value(card) + "万";
        }

        if (CardUtils.isTiao(card)) {
            return CardUtils.value(card) + "条";
        }

        if (CardUtils.isTong(card)) {
            return CardUtils.value(card) + "筒";
        }

        return null;
    }

    private interface Conditin {
        boolean run(String line);
    }

    private interface MsgBuilder {
        MahjongMsg build(String line);
    }

    private static void oneOperationExecute(String tips, Conditin condition, MsgBuilder msgBuilder) {
        oneOperationExecute(tips, condition, msgBuilder, false);
    }

    private static void oneOperationExecute(String tips, Conditin condition, MsgBuilder msgBuilder, boolean async) {
        DefaultEventLoopGroup executors = executorGroup;
        if (async) {
            executors = asyncExecutorGroup;
        }

        executors.execute(() -> {
            System.out.println(tips);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (condition.run(line)) {
                    MahjongMsg msg = msgBuilder.build(line);
                    MsgUtils.send(channel, msg);
                    break;
                } else {
                    System.out.println("错误的输入，请重新输入：");
                    System.out.println(tips);
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
                    System.out.println(tips);
                }
            }
        });
    }
}
