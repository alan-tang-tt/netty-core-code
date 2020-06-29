package com.imooc.netty.mahjong.client.mock;

import com.google.protobuf.ByteString;
import com.imooc.netty.mahjong.common.proto.*;
import com.imooc.netty.mahjong.common.util.CardUtils;
import com.imooc.netty.mahjong.common.util.MahjongContext;
import com.imooc.netty.mahjong.common.util.MessageUtils;
import com.imooc.netty.mahjong.common.util.OperationUtils;
import io.netty.channel.Channel;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class MockClient {

    private static final String MOCK_USER = "\r\n\r\n[mahjong@mock]$ ";

    // 用于读取用户输入
    private static Scanner scanner = new Scanner(System.in);
    // 当前玩家
    private static PlayerMsg player;
    // 当前房间
    private static RoomMsg room;

    public static void start(Channel channel) {
        MahjongContext.currentContext().setCurrentChannel(channel);
        // 发送hello消息
//        for (int i = 0; i < 1000; i++) {
            HelloRequest helloRequest = HelloRequest.newBuilder()
                    .setName("彤哥")
                    .build();
            MessageUtils.sendRequest(helloRequest);
//            try {
//                TimeUnit.MILLISECONDS.sleep(10);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }


        // 停顿2秒
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(2));

        // 请登录
        login();
    }

    public static void helloResponse(HelloResponse helloResponse) {
        System.out.println(helloResponse.getMessage());
    }

    private static void login() {
        oneOperation("\r\n请输入您的用户名和密码，以空格分隔：", line -> {
            if (!line.contains(" ")) {
                return false;
            }
            String[] arr = line.split(" ");
            // 发送登录消息
            LoginRequest request = LoginRequest.newBuilder()
                    .setUsername(arr[0])
                    .setPassword(arr[1])
                    .build();
            MessageUtils.sendRequest(request);
            return true;
        });
    }

    public static void loginResponse(LoginResponse message) {
        // 登录成功
        if (message.getResult()) {
            System.out.println("\r\n登录成功，您的信息为：");
            System.out.println(player = message.getPlayer());
            // 登录成功，选择创建房间还是加入房间
            createOrEnterRoom();
        } else {
            // 登录失败
            System.out.println("\r\n登录失败：" + message.getMessage() + "，请您重新登录。");
            login();
        }
    }

    private static void createOrEnterRoom() {
        twoOperationExecute("\r\n请选择您要进行的操作：\r\n1.创建房间\r\n2.加入房间",
                "\r\n请输入房间底分以及人数，以空格隔开：",
                true,
                line -> {
                    if (!line.contains(" ")) {
                        return false;
                    }
                    String[] arr = line.split(" ");
                    try {
                        int baseScore = Integer.parseInt(arr[0]);
                        int playerNum = Integer.parseInt(arr[1]);
                        CreateRoomRequest createRoomRequest = CreateRoomRequest.newBuilder()
                                .setBaseScore(baseScore)
                                .setPlayerNum(playerNum)
                                .build();
                        MessageUtils.sendRequest(createRoomRequest);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                },
                "\r\n请输入您要加入的房间号：",
                true,
                line -> {
                    try {
                        long roomId = Long.parseLong(line);
                        EnterRoomRequest enterRoomRequest = EnterRoomRequest.newBuilder()
                                .setRoomId(roomId)
                                .build();
                        MessageUtils.sendRequest(enterRoomRequest);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                });
    }

    public static void createRoomResponse(CreateRoomResponse message) {
        if (message.getResult()) {
            System.out.println("\r\n创建房间成功");
        } else {
            System.out.println("\r\n创建房间失败，请重新尝试");
            createOrEnterRoom();
        }
    }

    public static void enterRoomResponse(EnterRoomResponse message) {
        if (message.getResult()) {
            System.out.println("\r\n加入房间成功");
        } else {
            System.out.println("\r\n加入房间失败：" + message.getMessage() + "请重新尝试");
            createOrEnterRoom();
        }
    }

    public static void roomRefreshNotification(RoomRefreshNotification message) {
        RoomMsg room = message.getRoom();
        for (PlayerMsg roomPlayer : room.getPlayersList()) {
            if (roomPlayer != null && roomPlayer.getId() == player.getId()) {
                player = roomPlayer;
            }
        }
        if (MockClient.room == null) {
            MockClient.room = room;
            System.out.println("\r\n房间信息如下：");
            System.out.println(room);
            System.out.println("\r\n请耐心等待其他玩家加入房间~~");
        } else if (message.getOperation() == 0) {
            if (room.getStatus() == RoomStatus.STATUS_WAITING_PLAYER) {
                System.out.println("\r\n有新的玩家进入房间，刷新房间信息如下");
                System.out.println(room);
            } else if (room.getStatus() == RoomStatus.STATUS_GAME_STARTING) {
                System.out.println("\r\n游戏即将开始");
            } else if (room.getStatus() == RoomStatus.STATUS_GAME_OVER) {
                System.out.println("\r\n游戏结束了");
            } else {
                System.out.println("\r\n发牌完毕，牌局信息如下：");
                printRoom(room);
            }
        } else {
            if (message.getOperation() == OperationUtils.OPERATION_CHU) {
                System.out.println("\r\n刷新牌局信息（出）：");
            } else if (message.getOperation() == OperationUtils.OPERATION_PENG) {
                System.out.println("\r\n刷新牌局信息（碰）：");
            } else if (message.getOperation() == OperationUtils.OPERATION_GANG) {
                System.out.println("\r\n刷新牌局信息（杠）：");
            } else if (message.getOperation() == OperationUtils.OPERATION_GRAB) {
                System.out.println("\r\n刷新牌局信息（摸）：");
            }
            printRoom(room);
        }
    }

    private static boolean printRoom(RoomMsg room) {
        for (PlayerMsg roomPlayer : room.getPlayersList()) {
            if (roomPlayer != null) {
                byte[] cards = roomPlayer.getCards().toByteArray();
                Arrays.sort(cards);
                if (roomPlayer.getId() == player.getId()) {
                    player = roomPlayer.toBuilder().setCards(ByteString.copyFrom(cards)).build();
                    System.out.println("大佬您的牌：" + printCards(cards));
                } else {
                    System.out.println("玩家" + (roomPlayer.getPos() + 1) + "的牌：" + printCards(cards));
                }
            }
        }
        return false;
    }

    private static String printCards(byte... cards) {
        List<String> list = new ArrayList<>();
        int i = 1;
        for (byte card : cards) {
            if (card != 0) {
                list.add(toChinese(card) + "(" + i++ + ")");
            }
        }
        return list.toString();
    }

    private static String toChinese(byte card) {
        if (CardUtils.cardType(card) == CardUtils.CARD_TYPE_WAN_MASK) {
            return CardUtils.cardValue(card) + "万";
        }

        if (CardUtils.cardType(card) == CardUtils.CARD_TYPE_TIAO_MASK) {
            return CardUtils.cardValue(card) + "条";
        }

        if (CardUtils.cardType(card) == CardUtils.CARD_TYPE_TONG_MASK) {
            return CardUtils.cardValue(card) + "筒";
        }

        return null;
    }

    public static void operationNotification(OperationNotification message) {
        if (message.getPos() == player.getPos()) {
            if (message.getOperation() == OperationUtils.OPERATION_CHU) {
                chu(message);
            } else {
                operate(message);
            }
        } else {
            if (message.getOperation() == OperationUtils.OPERATION_CHU) {
                System.out.println("\r\n请等待 玩家" + (message.getPos() + 1) + " 出牌");
            } else {
                System.out.println("\r\n请等待其他玩家操作");
            }
        }
    }

    private static void operate(OperationNotification message) {
        // 一个玩家同时最多可拥有：碰、杠、胡、过，四种操作
        int operation = message.getOperation();
        StringBuilder sb = new StringBuilder("\r\n请您操作：");
        int i = 1;
        Map<Integer, Command> map = new HashMap<>();
        if (hasOp(operation, OperationUtils.OPERATION_PENG)) {
            Command command = line -> {
                OperationRequest request = OperationRequest.newBuilder()
                        .setOperation(OperationUtils.OPERATION_PENG)
                        .setCard(message.getFireCard())
                        .setPos(player.getPos())
                        .build();
                MessageUtils.sendRequest(request);
                return true;
            };
            map.put(i, command);
            sb.append("\r\n").append(i++).append(".碰 ");
        }

        if (hasOp(operation, OperationUtils.OPERATION_GANG)) {
            Command command = line -> {
                OperationRequest request = OperationRequest.newBuilder()
                        .setOperation(OperationUtils.OPERATION_GANG)
                        .setCard(message.getFireCard())
                        .setPos(player.getPos())
                        .build();
                MessageUtils.sendRequest(request);
                return true;
            };
            map.put(i, command);
            sb.append("\r\n").append(i++).append(".杠 ");
        }

        if (hasOp(operation, OperationUtils.OPERATION_HU)) {
            Command command = line -> {
                OperationRequest request = OperationRequest.newBuilder()
                        .setOperation(OperationUtils.OPERATION_HU)
                        .setCard(message.getFireCard())
                        .setPos(player.getPos())
                        .build();
                MessageUtils.sendRequest(request);
                return true;
            };
            map.put(i, command);
            sb.append("\r\n").append(i++).append(".胡 ");
        }

        if (hasOp(operation, OperationUtils.OPERATION_GUO)) {
            Command command = line -> {
                OperationRequest request = OperationRequest.newBuilder()
                        .setOperation(OperationUtils.OPERATION_GUO)
                        .setCard(message.getFireCard())
                        .setPos(player.getPos())
                        .build();
                MessageUtils.sendRequest(request);
                return true;
            };
            map.put(i, command);
            sb.append("\r\n").append(i++).append(".过 ");
        }

        System.out.print(sb.toString() + MOCK_USER);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            try {
                Integer num = Integer.parseInt(line);
                Command command = map.get(num);
                if (command != null && command.run(line)) {
                    break;
                }
            } catch (Exception e) {
                // just ignore
            }
            System.out.println("\r\n错误的输入，请重新输入："+MOCK_USER);
        }
    }

    private static boolean hasOp(int operation, int op) {
        return (operation & op) == op;
    }

    private static void chu(OperationNotification message) {
        oneOperation("\r\n请您出牌，输入括号中的数字即可：",
                line -> {
                    try {
                        int num = Integer.parseInt(line);

                        byte card = 0;
                        int i = 1;
                        for (byte c : player.getCards()) {
                            if (c != 0) {
                                if (i++ == num) {
                                    card = c;
                                    break;
                                }
                            }
                        }

                        OperationRequest operationRequest = OperationRequest.newBuilder()
                                .setOperation(OperationUtils.OPERATION_CHU)
                                .setPos(player.getPos())
                                .setCard(card)
                                .build();
                        MessageUtils.sendRequest(operationRequest);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                });
    }

    public static void operationResultNotification(OperationResultNotification message) {
        if (message.getPos() == player.getPos()) {
            if (message.getOperation() == OperationUtils.OPERATION_GRAB) {
                System.out.println("\r\n您摸了一张牌");
            } else {
                System.out.println("\r\n您 " + operation(message.getOperation()) + " 了 " + printCards((byte) message.getCard()));
            }
        } else {
            if (message.getOperation() == OperationUtils.OPERATION_GRAB) {
                System.out.println("\r\n玩家" + (message.getPos() + 1) + "摸了一张牌");
            } else {
                System.out.println("\r\n玩家" + (message.getPos() + 1) + " " + operation(message.getOperation()) + " 了 " + printCards((byte) message.getCard()));
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
        if (operation == OperationUtils.OPERATION_GRAB) {
            return "摸";
        }
        return null;
    }

    public static void gameOverNotification(GameOverNotification message) {
        System.out.println("\r\n游戏结束，各玩家手牌如下：");
        printRoom(message.getRoom());
        room = null;
    }

    public static void settlementNotification(SettlementNotification message) {
        System.out.println("\r\n结算详情");
        List<Integer> scores = message.getScoresList();
        for (int i = 0; i < scores.size(); i++) {
            int score = scores.get(i);
            if (i == player.getPos()) {
                System.out.println("您：" + score);
            } else {
                System.out.println("玩家" + (i + 1) + "：" + score);
            }
        }
        createOrEnterRoom();
    }

    private interface Command {
        boolean run(String line);
    }

    private static void oneOperation(String tips, Command command) {
        // 打印提示
        System.out.print(tips + MOCK_USER);
        while (scanner.hasNextLine()) {
            // 读取用户输入
            String line = scanner.nextLine();
            // 处理失败，则提示重新输入
            if (!command.run(line)) {
                System.out.println("\r\n错误的输入，请重新输入：");
            } else {
                // 处理成功，跳出循环
                break;
            }
        }
    }

    private static void twoOperationExecute(String tips, String tips1, boolean hasParam1, Command command1, String tips2, boolean hasParam2, Command command2) {
        System.out.print(tips + MOCK_USER);
        boolean flag1 = false;
        boolean flag2 = false;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (!flag1 && !flag2 && "1".equals(line)) {
                flag1 = true;
                System.out.print(tips1 + MOCK_USER);
                if (!hasParam1 && command1.run(line)) {
                    break;
                }
            } else if (!flag1 && !flag2 && "2".equals(line)) {
                flag2 = true;
                System.out.print(tips2 + MOCK_USER);
                if (!hasParam2 && command2.run(line)) {
                    break;
                }
            } else if (flag1 && command1.run(line)) {
                break;
            } else if (flag2 && command2.run(line)) {
                break;
            } else {
                System.out.println("\r\n错误的输入，请重新输入：");
            }
        }
    }
}
