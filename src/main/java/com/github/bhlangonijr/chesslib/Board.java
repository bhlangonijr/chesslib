/*
 * Copyright 2017 Ben-Hur Carlos Vieira Langoni Junior
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.bhlangonijr.chesslib;

import com.github.bhlangonijr.chesslib.game.GameContext;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveList;

import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Chessboard data structure
 */
public class Board implements Cloneable, BoardEvent {

    private final LinkedList<MoveBackup> backup;
    private final EnumMap<BoardEventType, List<BoardEventListener>> eventListener;
    private final long bitboard[];
    private final long bbSide[];
    private final EnumMap<Side, CastleRight> castleRight;
    private final LinkedList<Integer> history = new LinkedList<Integer>();
    private Side sideToMove;
    private Square enPassantTarget;
    private Square enPassant;
    private Integer moveCounter;
    private Integer halfMoveCounter;
    private GameContext context;
    private boolean enableEvents;
    private boolean updateHistory;

    /**
     * 32-bit hash keys for repetition detection. They have been tested so that
     * (1) no xor'ed pair of keys collides with another existing key
     * (2) no xor'ed pair of keys collides with a xor of another pair of keys
     * (3) no xor of three keys collides with another single key
     */

    private static final int shortKeys [] = {
            -702565069, -651716404, -90772002, 139827217, -818005229, 1941803680, -1748144377, 596276289,
            1921378528, 1368035031, -1425636917, -575780564, 1409290471, 845303300, 1136883922, -1430851484,
            924912986, 92095816, -1065598504, -748528041, 1089599880, -723160118, 598513820, -1833342420,
            -906682537, -1353481056, 1170562732, 879692537, -1717334298, -2107126715, -1800285465, -2019153999,
            -312376001, -1972445292, -1934743398, -1607487485, 923393733, 1080390386, 36105524, 93848421,
            356787436, -660305748, 1469707869, 640989862, 2057833544, -1142459402, 853007415, 1784621122,
            781779923, -1144725482, 273392100, -682150231, -256820337, 1136592960, -942170213, -1018707522,
            1847610686, -1601831344, -1276646715, -346590898, 488806964, 1313067790, -1682129807, -341730302,
            612847047, 1095071911, -445450136, 1920139068, 145389739, -643893363, 1241636357, -1100133313,
            -184365430, 1319278410, 337755483, 1168851636, -2018124883, 1987138910, -130098383, -465363916,
            1261447690, 525217523, -636830992, 1346028279, -956871218, 538206404, 649095846, 1324071018,
            1081035944, -613597020, 1165899758, -1992686127, 775621122, 1788875486, -1115851423, 1026827060,
            -1049617314, -1819399049, 44482663, 1565463311, 967598703, -1176084252, 532537143, -1736176902,
            1968791497, 1942159074, 1553299834, 1540848062, -1077798375, -1957540514, -748422607, -1835034361,
            -781018150, -1237809038, 621622304, -1852382785, -44645642, 1394279158, 1641190693, 1031255177,
            1970948249, -874529702, 363532650, 149151584, -2019253001, 1233445122, -1942132566, -1605421159,
            -1510841015, -1047551057, 1756701190, -1429453207, -391100556, 1575039684, -597592416, -427242900,
            1458115053, -1658987226, -1870818275, 1265802426, 784056402, 398561731, -137806895, -325193710,
            1031928386, -1125649578, -1191639162, -1087940938, 237315661, -802974450, -1205409558, -1633811032,
            -1823701736, -1076650840, -2050061006, -1938726381, 267238767, 25562894, 328584262, 479314329,
            -1261740071, 443634181, 1325392080, -1598538064, -1796555619, -2105530078, -2044272375, 1479666509,
            -1397042900, -115086320, -436538223, 10455911, -251368917, 1323367057, 1128178934, 646330152,
            -1820447081, 821315824, 1681259512, -1147758308, 1292400502, -1094239026, 561314726, 1813924242,
            1643130750, -73302279, -1580351986, 1567334350, 391193720, -1610484026, 1022802322, -1387355731,
            1638630150, -1733967905, 1252765769, -1339703919, 412765787, 375486482, -281160976, 1573501602,
            -796499356, -315806340, 1452897483, 1313950575, 56822976, 1498364758, 115182057, -310987306,
            -244167865, -2075821809, -1404706844, 791753872, 1550371939, 1585852137, -1398559253, 2055483213,
            -1804937319, -877391321, 1016423629, 276495527, -1443109447, -1099753510, 241473351, -2019634279,
            -747807769, -388298369, -612016835, -178185214, -519567089, 1797551461, -948620493, -1010109542,
            -1513604317, -1324175055, 1390568897, 993908495, -646088337, -2042396723, -2069962402, -1101902345,
            -280474176, -966988739, -574864907, 1336179347, 1861133545, 1770875011, -1771922371, 791235088,
            -10354762, 1878305797, -735803689, -845399180, -1386716429, -319718319, -392854706, -38065418,
            411961145, -1878873953, 1512215674, 1177623758, -1531141041, 554960052, 541415161, -602723817,
            805903354, -1995273444, -1185420709, -1434322407, 603655165, 153114746, -539677199, -1163592871,
            279257638, -1919298805, 868230627, 1804438929, -1609150095, 1092584137, -1202305351, 1115014665,
            -1198899467, -1878782400, -1963582840, -1875233927, 434854463, -1726026085, 971518002, 1802126041,
            52814043, -1086403409, 951465067, 243643524, -1012272282, 1413207580, 1801972914, -937402570,
            5946113, -2082278335, -957118455, 1576487433, 1087638029, 922897015, 866267613, -401803471,
            -896211428, -525560087, -117732527, -948483772, 1956577158, -976662464, 1875770093, 1175886155,
            1266535603, 963027096, -1807270736, -374422132, 610611732, 468162457, 1538912940, -265734539,
            -1558682322, -1420360404, -672887181, 245604288, -1460097832, 849334401, -1646635064, 2072639472,
            1819522712, 686145503, 1767118689, 464618777, -902351590, 1039541071, 2124904519, 150627268,
            -823534248, 1806289825, 340343120, -1868845315, 1425605642, -2144706846, -631719414, 822877016,
            666368798, 1382340178, 305839815, -669603956, -2022541365, -684781649, 1145076213, -2041976051,
            -2004438671, 179622289, 48101916, 1063681041, 33788223, -864150665, -1690994975, -2105465024,
            2088626432, 856157054, -46286949, -1682122209, 1760548685, 282679467, -317315719, -1370610814,
            -459959968, 1088243831, -2108490466, -1036609405, -1337965331, 229956447, 1927355403, -1294154332,
            -523607208, 1717164185, 558206845, -727558932, 1323301943, -746017714, 1605287365, 1977952885,
            -994318767, 2048830511, -1239285873, 562034380, -1913069048, 1717636131, 1754677583, -784126254,
            1439203816, 1561079451, -950218527, -785464705, 948235908, -47304947, -110498939, 2063837736,
            -1418243506, 470877008, -17709309, -1377269971, 1818790784, 1906970716, 188796215, 1981525322,
            1790044894, 1092359542, 1072398152, -1505504503, 1596046814, 1745240895, -1845600496, 63785546,
            233669995, -424086603, -1818167112, 1276280538, -543694031, 2014621888, 1367664251, 1581869180,
            770761050, -1383581777, 1440989036, 178500078, -1356722378, -1012601296, 467919391, -1371987389,
            -889895089, 1610239154, -1824910271, -565661564, -884472880, -1732344531, 1511419217, -670652047,
            -62615450, -1761856474, 1798589496, -1588443586, -1787131739, 2109434206, -557279962, -931995189,
            -1214758909, -1244894258, 1979853247, 1702014861, -290100210, -408514082, -249878590, 933253093,
            2009515607, 358285221, 1769664764, 846435124, 223169264, 1518531551, -808417067, -326211284,
            1453526341, 838923638, 499036940, -1389659866, -1557871046, -60929106, 269663959, -200302741,
            775349136, -655356035, 1655217579, -1032648320, -1478002511, 1228412982, 1494041379, -774246152,
            1146144875, -371511256, 439901195, 2051907664, 1629651801, 492870188, 1812449529, -876760463,
            -770244712, 374795604, 164936890, -1613986400, 74700012, -1206184420, -1901690503, -1895083606,
            -1491382153, -1617727820, -188934920, -1603862366, 1743176918, 1018302795, -1542031864, 749866761,
            1351381133, 1813474667, 2341101, -1257410222, 1219800950, 673364176, -1541534705, 201479559,
            -1323345257, 1553236608, -1975509742, 339356428, 1955301094, -139113187, 1777128926, -250727194,
            -1529735051, -1479599550, -1813330201, -871631412, -1673195160, -525477335, -410997848, -1622425094,
            1410679692, -1712212683, 533030723, -1567232134, 1684922110, 1123211354, -1444837745, 990585627,
            272408843, 1084438887, 627750901, -1545697330, -1501441840, 2132995975, 378997177, -879812924,
            102882230, 1717397981, -1261670548, -220656848, -25691576, 1578629283, 319868447, 859825243,
            1309653270, 1701595822, -1789604753, 1862597576, -55108142, -818534934, -1216213885, -1356334640,
            2019375208, 447561753, -452622860, 589076951, 1617589666, 1602791952, -1437050240, -1894147899,
            1180142771, 1218481632, 1620808049, -1928740283, 70364333, -839378435, -1855585647, -2060559734,
            -842938200, -131527192, -1706474775, -813766134, -1113625075, -1463422654, 1740096306, 100969361,
            1963047800, 2125971286, -201096828, 356032625, 1467464468, 1375391945, 355749486, 2069777971,
            1362329373, -1753608929, -809260167, 1497740900, -2098961626, 1496769884, -476569855, -2110185363,
            -1216194927, 1051002209, 179913238, 473697988, -1998585859, -133497550, -256287676, 1740233978,
            414161443, -396320928, -677063676, -950300265, 786423033, 1902675783, -1692145856, 332044973,
            -1420131427, 1782972590, -1241717549, 750713866, -492364451, -173450708, 1292054945, -791371911,
            -673191761, 1734208085, 1360592851, -1707098522, -2097477858, -215444385, -674873053, -64034250,
            554120659, 722151756, 844243630, 408490533, 108765075, 316885181, -2076991590, 1532630432,
            -1241728822, -3871488, -831628873, 1689084349, 35417000, -1155558178, -1443139417, -839890531,
            1705762241, 910773525, 359329992, -1216992764, -1204938540, 1379353055, 1436278025, 1512220878,
            1670969808, 1693343875, 1502212758, 797272870, 1875018785, -1290267933, -1699240068, -1520053719,
            313208943, -1302125494, -1968807542, 990840025, -1422621405, -111252796, -1793799015, 629801043,
            -1083023387, -1330346956, 858937220, -1706920332, 1907097282, -509975834, -1199047353, -1971906628,
            1222657810, -366475359, -1789494759, -462821105, 590699027, -2120021070, 1918480178, -887662984,
            -1349225843, -858103256, 545831156, -484973205, 892905490, 234855701, -923332057, -1782382334,
            -76753291, -1230951951, 515725375, -1474118352, 341784322, 2111989453, 1869939948, 1570966501,
            -93114079, 2094770206, -829540968, 838077186, 1183801227, 1034667295, -1366764251, -442861168,
            -1804474693, -81629767, 1243366786, -594428051, -646278283, -1526405242, 686221453, 372623104,
            568130968, -1013174072, -1641184673, 1858985781, -777049255, 1834416915, 663860650, 2106666450,
            -423798316, -1492014558, 1802169576,  670009693, 275948276, -2136905904, 1455848813, -428955569,
            -519611950, -1559659699, 751854940, -4572983, -1615791950, -923141036, -1962400809, 942704084,
            922044431, -1433808400, -606386080, 696210099, 1536819315, 1603327493, -1644798329, 198911974,
            2084080181, 368153161, 964257884, 912710436, 597074072, 1234873272, -355403114, -76697025,
            918219555, -181174754, 1015084871, 266192105, 1262689892, -513119737, -1335274479, 430841744,
            123456789};

    /**
     * Instantiates a new Board.
     */
    public Board() {
        this(new GameContext(), false);
    }

    /**
     * Instantiates a new Board.
     *
     * @param gameContext   the game context
     * @param updateHistory the update history
     */
    public Board(GameContext gameContext, boolean updateHistory) {
        bitboard = new long[Piece.values().length];
        bbSide = new long[Side.values().length];
        castleRight = new EnumMap<Side, CastleRight>(Side.class);
        backup = new LinkedList<MoveBackup>();
        context = gameContext;
        eventListener = new EnumMap<BoardEventType, List<BoardEventListener>>(BoardEventType.class);
        this.updateHistory = updateHistory;
        setSideToMove(Side.WHITE);
        setEnPassantTarget(Square.NONE);
        setEnPassant(Square.NONE);
        setMoveCounter(1);
        setHalfMoveCounter(0);
        for (BoardEventType evt : BoardEventType.values()) {
            eventListener.put(evt, new CopyOnWriteArrayList<BoardEventListener>());
        }
        loadFromFen(gameContext.getStartFEN());
        setEnableEvents(true);
    }

    private static Square findEnPassantTarget(Square sq, Side side) {
        Square ep = Square.NONE;
        if (!Square.NONE.equals(sq)) {
            ep = Side.WHITE.equals(side) ?
                    Square.encode(Rank.RANK_5, sq.getFile()) :
                    Square.encode(Rank.RANK_4, sq.getFile());
        }
        return ep;
    }

    private static Square findEnPassant(Square sq, Side side) {
        Square ep = Square.NONE;
        if (!Square.NONE.equals(sq)) {
            ep = Side.WHITE.equals(side) ?
                    Square.encode(Rank.RANK_3, sq.getFile()) :
                    Square.encode(Rank.RANK_6, sq.getFile());
        }
        return ep;
    }

    /**
     * Is promo rank boolean.
     *
     * @param side the side
     * @param move the move
     * @return the boolean
     */
    public static boolean isPromoRank(Side side, Move move) {
        if (side.equals(Side.WHITE) &&
                move.getTo().getRank().equals(Rank.RANK_8)) {
            return true;
        } else return side.equals(Side.BLACK) &&
                move.getTo().getRank().equals(Rank.RANK_1);

    }

    /**
     * Execute the move in the board
     *
     * @param move the move
     * @return the boolean
     */
    public boolean doMove(final Move move) {
        return doMove(move, false);
    }

    /**
     * Execute the move on the board
     *
     * @param move           the move
     * @param fullValidation the full validation
     * @return the boolean
     */
    public boolean doMove(final Move move, boolean fullValidation) {

        if (!isMoveLegal(move, fullValidation)) {
            return false;
        }

        Piece movingPiece = getPiece(move.getFrom());
        Side side = getSideToMove();

        MoveBackup backupMove = new MoveBackup(this, move);
        final boolean isCastle = context.isCastleMove(move);

        if (PieceType.KING.equals(movingPiece.getPieceType())) {
            if (isCastle) {
                if (context.hasCastleRight(move, getCastleRight(side))) {
                    CastleRight c = context.isKingSideCastle(move) ? CastleRight.KING_SIDE :
                            CastleRight.QUEEN_SIDE;
                    Move rookMove = context.getRookCastleMove(side, c);
                    movePiece(rookMove, backupMove);
                } else {
                    return false;
                }
            }
            getCastleRight().put(side, CastleRight.NONE);
        } else if (PieceType.ROOK.equals(movingPiece.getPieceType())
                && !CastleRight.NONE.equals(getCastleRight(side))) {
            final Move oo = context.getRookoo(side);
            final Move ooo = context.getRookooo(side);

            if (move.getFrom().equals(oo.getFrom())) {
                if (CastleRight.KING_AND_QUEEN_SIDE.equals(getCastleRight(side))) {
                    getCastleRight().put(side, CastleRight.QUEEN_SIDE);
                } else if (CastleRight.KING_SIDE.equals(getCastleRight(side))) {
                    getCastleRight().put(side, CastleRight.NONE);
                }
            } else if (move.getFrom().equals(ooo.getFrom())) {
                if (CastleRight.KING_AND_QUEEN_SIDE.equals(getCastleRight(side))) {
                    getCastleRight().put(side, CastleRight.KING_SIDE);
                } else if (CastleRight.QUEEN_SIDE.equals(getCastleRight(side))) {
                    getCastleRight().put(side, CastleRight.NONE);
                }
            }
        }

        Piece capturedPiece = movePiece(move, backupMove);

        if (PieceType.ROOK.equals(capturedPiece.getPieceType())) {
            final Move oo = context.getRookoo(side.flip());
            final Move ooo = context.getRookooo(side.flip());
            if (move.getTo().equals(oo.getFrom())) {
                if (CastleRight.KING_AND_QUEEN_SIDE.equals(getCastleRight(side.flip()))) {
                    getCastleRight().put(side.flip(), CastleRight.QUEEN_SIDE);
                } else if (CastleRight.KING_SIDE.equals(getCastleRight(side.flip()))) {
                    getCastleRight().put(side.flip(), CastleRight.NONE);
                }
            } else if (move.getTo().equals(ooo.getFrom())) {
                if (CastleRight.KING_AND_QUEEN_SIDE.equals(getCastleRight(side.flip()))) {
                    getCastleRight().put(side.flip(), CastleRight.KING_SIDE);
                } else if (CastleRight.QUEEN_SIDE.equals(getCastleRight(side.flip()))) {
                    getCastleRight().put(side.flip(), CastleRight.NONE);
                }
            }
        }

        if (Piece.NONE.equals(capturedPiece)) {
            setHalfMoveCounter(getHalfMoveCounter() + 1);
        } else {
            setHalfMoveCounter(0);
        }

        setEnPassantTarget(Square.NONE);
        setEnPassant(Square.NONE);

        if (PieceType.PAWN.equals(movingPiece.getPieceType())) {
            if (Math.abs(move.getTo().getRank().ordinal() -
                    move.getFrom().getRank().ordinal()) == 2) {
                Piece otherPawn = Side.WHITE.equals(side) ?
                        Piece.BLACK_PAWN : Piece.WHITE_PAWN;
                if (hasPiece(otherPawn, move.getTo().getSideSquares())) {
                    setEnPassantTarget(move.getTo());
                }
                setEnPassant(findEnPassant(move.getTo(), side));
            }
            setHalfMoveCounter(0);
        }

        if (side == Side.BLACK) {
            setMoveCounter(getMoveCounter() + 1);
        }

        setSideToMove(side.flip());
        if (updateHistory) {
            getHistory().addLast(this.hashCode());
        }

        backup.add(backupMove);
        //call listeners
        if (isEnableEvents() &&
                eventListener.get(BoardEventType.ON_MOVE).size() > 0) {
            for (BoardEventListener evl :
                    eventListener.get(BoardEventType.ON_MOVE)) {
                evl.onEvent(move);
            }
        }
        return true;
    }

    /**
     * Undo the last move executed on the board
     *
     * @return the move
     */
    public Move undoMove() {
        Move move = null;
        final MoveBackup b = backup.removeLast();
        if (updateHistory) {
            getHistory().removeLast();
        }
        if (b != null) {
            move = b.getMove();
            b.restore(this);
        }
        //call listeners
        if (isEnableEvents() &&
                eventListener.get(BoardEventType.ON_UNDO_MOVE).size() > 0) {
            for (BoardEventListener evl :
                    eventListener.get(BoardEventType.ON_UNDO_MOVE)) {
                evl.onEvent(b);
            }
        }
        return move;
    }

    /**
     * Move piece piece.
     *
     * @param move   the move
     * @param backup the backup
     * @return the piece
     */
    /*
     * Move a piece
     * @param move
     * @return
     */
    protected Piece movePiece(Move move, MoveBackup backup) {
        return movePiece(move.getFrom(), move.getTo(), move.getPromotion(), backup);
    }

    /**
     * Move piece piece.
     *
     * @param from      the from
     * @param to        the to
     * @param promotion the promotion
     * @param backup    the backup
     * @return the piece
     */
    protected Piece movePiece(Square from, Square to, Piece promotion, MoveBackup backup) {
        Piece movingPiece = getPiece(from);
        Piece capturedPiece = getPiece(to);

        unsetPiece(movingPiece, from);
        if (!Piece.NONE.equals(capturedPiece)) {
            unsetPiece(capturedPiece, to);
        }
        if (!Piece.NONE.equals(promotion)) {
            setPiece(promotion, to);
        } else {
            setPiece(movingPiece, to);
        }

        if (PieceType.PAWN.equals(movingPiece.getPieceType()) &&
                !Square.NONE.equals(getEnPassantTarget()) &&
                !to.getFile().equals(from.getFile()) &&
                Piece.NONE.equals(capturedPiece)) {
            capturedPiece = getPiece(getEnPassantTarget());
            if (backup != null && !Piece.NONE.equals(capturedPiece)) {
                unsetPiece(capturedPiece, getEnPassantTarget());
                backup.setCapturedSquare(getEnPassantTarget());
                backup.setCapturedPiece(capturedPiece);
            }
        }
        return capturedPiece;
    }

    /**
     * Undo move piece.
     *
     * @param move the move
     */
    protected void undoMovePiece(Move move) {
        Square from = move.getFrom();
        Square to = move.getTo();
        Piece promotion = move.getPromotion();
        Piece movingPiece = getPiece(to);

        unsetPiece(movingPiece, to);

        if (!Piece.NONE.equals(promotion)) {
            setPiece(Piece.make(getSideToMove(), PieceType.PAWN), from);
        } else {
            setPiece(movingPiece, from);
        }
    }

    /**
     * Returns true if finds the specific piece in the given square locations
     *
     * @param piece    the piece
     * @param location the location
     * @return boolean
     */
    public boolean hasPiece(Piece piece, Square location[]) {
        for (Square sq : location) {
            if (piece.equals(getPiece(sq))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Clear the entire board
     */
    public void clear() {
        setSideToMove(Side.WHITE);
        setEnPassantTarget(Square.NONE);
        setEnPassant(Square.NONE);
        setMoveCounter(0);
        setHalfMoveCounter(0);
        getHistory().clear();

        for (int i = 0; i < bitboard.length; i++) {
            bitboard[i] = 0L;
        }
        for (int i = 0; i < bbSide.length; i++) {
            bbSide[i] = 0L;
        }
        backup.clear();
    }

    /**
     * add a piece into a given square
     *
     * @param piece the piece
     * @param sq    the sq
     */
    public void setPiece(Piece piece, Square sq) {
        bitboard[piece.ordinal()] |= sq.getBitboard();
        bbSide[piece.getPieceSide().ordinal()] |= sq.getBitboard();
    }

    /**
     * remove a piece from a given square
     *
     * @param piece the piece
     * @param sq    the sq
     */
    public void unsetPiece(Piece piece, Square sq) {
        bitboard[piece.ordinal()] ^= sq.getBitboard();
        bbSide[piece.getPieceSide().ordinal()] ^= sq.getBitboard();
    }

    /**
     * Load an specific chess position using FEN notation
     * ex.: rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1
     *
     * @param fen the fen
     */
    public void loadFromFen(String fen) {
        clear();
        String squares = fen.substring(0, fen.indexOf(' '));
        String state = fen.substring(fen.indexOf(' ') + 1);

        String ranks[] = squares.split("/");
        int file;
        int rank = 7;
        for (String r : ranks) {
            file = 0;
            for (int i = 0; i < r.length(); i++) {
                char c = r.charAt(i);
                if (Character.isDigit(c)) {
                    file += Integer.parseInt(c + "");
                } else {
                    Square sq = Square.encode(Rank.values()[rank], File.values()[file]);
                    setPiece(Constants.getPieceByNotation(c + ""), sq);
                    file++;
                }
            }
            rank--;
        }

        sideToMove = state.toLowerCase().charAt(0) == 'w' ? Side.WHITE : Side.BLACK;

        if (state.contains("KQ")) {
            castleRight.put(Side.WHITE, CastleRight.KING_AND_QUEEN_SIDE);
        } else if (state.contains("K")) {
            castleRight.put(Side.WHITE, CastleRight.KING_SIDE);
        } else if (state.contains("Q")) {
            castleRight.put(Side.WHITE, CastleRight.QUEEN_SIDE);
        } else {
            castleRight.put(Side.WHITE, CastleRight.NONE);
        }

        if (state.contains("kq")) {
            castleRight.put(Side.BLACK, CastleRight.KING_AND_QUEEN_SIDE);
        } else if (state.contains("k")) {
            castleRight.put(Side.BLACK, CastleRight.KING_SIDE);
        } else if (state.contains("q")) {
            castleRight.put(Side.BLACK, CastleRight.QUEEN_SIDE);
        } else {
            castleRight.put(Side.BLACK, CastleRight.NONE);
        }

        String flags[] = state.split(" ");

        if (flags.length >= 3) {
            String s = flags[2].toUpperCase().trim();
            if (!s.equals("-")) {
                Square ep = Square.valueOf(s);
                setEnPassant(ep);
                setEnPassantTarget(findEnPassantTarget(ep, sideToMove));
            } else {
                setEnPassant(Square.NONE);
                setEnPassantTarget(Square.NONE);
            }
            if (flags.length >= 4) {
                halfMoveCounter = Integer.parseInt(flags[3]);
                if (flags.length >= 5) {
                    moveCounter = Integer.parseInt(flags[4]);
                }
            }
        }

        //call listeners
        if (isEnableEvents() &&
                eventListener.get(BoardEventType.ON_LOAD).size() > 0) {
            for (BoardEventListener evl :
                    eventListener.get(BoardEventType.ON_LOAD)) {
                evl.onEvent(Board.this);
            }
        }
    }

    /**
     * Generates the current board FEN representation
     *
     * @return board fen
     */
    public String getFen() {
        return getFen(true);
    }

    /**
     * Generates the current board FEN representation
     *
     * @param includeCounters if true include halfMove and fullMove counters
     * @return board fen
     */
    public String getFen(boolean includeCounters) {

        StringBuffer fen = new StringBuffer();
        int count = 0;
        int rankCounter = 1;
        int sqCount = 0;
        for (int i = 7; i >= 0; i--) {
            Rank r = Rank.values()[i];
            for (int n = 0; n <= 7; n++) {
                File f = File.values()[n];
                if (!File.NONE.equals(f) && !Rank.NONE.equals(r)) {
                    Square sq = Square.encode(r, f);
                    Piece piece = getPiece(sq);
                    if (!Piece.NONE.equals(piece)) {
                        if (count > 0) {
                            fen.append(count);
                        }
                        fen.append(Constants.getPieceNotation(piece));
                        count = 0;
                    } else {
                        count++;
                    }
                    if ((sqCount + 1) % 8 == 0) {
                        if (count > 0) {
                            fen.append(count);
                            count = 0;
                        }
                        if (rankCounter < 8) {
                            fen.append("/");
                        }
                        rankCounter++;
                    }
                    sqCount++;
                }
            }
        }

        if (Side.WHITE.equals(sideToMove)) {
            fen.append(" w");
        } else {
            fen.append(" b");
        }

        String rights = "";
        if (CastleRight.KING_AND_QUEEN_SIDE.
                equals(castleRight.get(Side.WHITE))) {
            rights += "KQ";
        } else if (CastleRight.KING_SIDE.
                equals(castleRight.get(Side.WHITE))) {
            rights += "K";
        } else if (CastleRight.QUEEN_SIDE.
                equals(castleRight.get(Side.WHITE))) {
            rights += "Q";
        }

        if (CastleRight.KING_AND_QUEEN_SIDE.
                equals(castleRight.get(Side.BLACK))) {
            rights += "kq";
        } else if (CastleRight.KING_SIDE.
                equals(castleRight.get(Side.BLACK))) {
            rights += "k";
        } else if (CastleRight.QUEEN_SIDE.
                equals(castleRight.get(Side.BLACK))) {
            rights += "q";
        }

        if (rights.equals("")) {
            fen.append(" -");
        } else {
            fen.append(" " + rights);
        }

        if (Square.NONE.equals(getEnPassant())) {
            fen.append(" -");
        } else {
            fen.append(" ");
            fen.append(getEnPassant().toString().toLowerCase());
        }

        if (includeCounters) {
            fen.append(" ");
            fen.append(getHalfMoveCounter());

            fen.append(" ");
            fen.append(getMoveCounter());
        }

        return fen.toString();
    }

    /**
     * Get the piece on the given square
     *
     * @param sq the sq
     * @return piece
     */
    public Piece getPiece(Square sq) {
        for (int i = 0; i < bitboard.length - 1; i++) {
            if ((sq.getBitboard() & bitboard[i]) != 0L) {
                return Piece.values()[i];
            }
        }
        return Piece.NONE;
    }

    /**
     * Gets bitboard.
     *
     * @return the bitboard
     */
    public long getBitboard() {
        return bbSide[0] | bbSide[1];
    }

    /**
     * Gets bitboard.
     *
     * @param piece the piece
     * @return the bitboard of a given piece
     */
    public long getBitboard(Piece piece) {
        return bitboard[piece.ordinal()];
    }

    /**
     * Gets bitboard.
     *
     * @param side the side
     * @return the bitboard of a given side
     */
    public long getBitboard(Side side) {
        return bbSide[side.ordinal()];
    }

    /**
     * Get bb side long [ ].
     *
     * @return the bbSide
     */
    public long[] getBbSide() {
        return bbSide;
    }

    /**
     * Get the square(s) location of the given piece
     *
     * @param piece the piece
     * @return piece location
     */
    public List<Square> getPieceLocation(Piece piece) {
        if (getBitboard(piece) != 0L) {
            return Bitboard.bbToSquareList(getBitboard(piece));
        }
        return Collections.emptyList();

    }

    /**
     * Gets side to move.
     *
     * @return the sideToMove
     */
    public Side getSideToMove() {
        return sideToMove;
    }

    /**
     * Sets side to move.
     *
     * @param sideToMove the sideToMove to set
     */
    public void setSideToMove(Side sideToMove) {
        this.sideToMove = sideToMove;
    }

    /**
     * Gets en passant target.
     *
     * @return the enPassantTarget
     */
    public Square getEnPassantTarget() {
        return enPassantTarget;
    }

    /**
     * Sets en passant target.
     *
     * @param enPassant the enPassantTarget to set
     */
    public void setEnPassantTarget(Square enPassant) {
        this.enPassantTarget = enPassant;
    }

    /**
     * Gets en passant.
     *
     * @return the enPassant
     */
    public Square getEnPassant() {
        return enPassant;
    }

    /**
     * Sets en passant.
     *
     * @param enPassant the enPassant to set
     */
    public void setEnPassant(Square enPassant) {
        this.enPassant = enPassant;
    }

    /**
     * Gets move counter.
     *
     * @return the moveCounter
     */
    public Integer getMoveCounter() {
        return moveCounter;
    }

    /**
     * Sets move counter.
     *
     * @param moveCounter the moveCounter to set
     */
    public void setMoveCounter(Integer moveCounter) {
        this.moveCounter = moveCounter;
    }

    /**
     * Gets half move counter.
     *
     * @return the halfMoveCounter
     */
    public Integer getHalfMoveCounter() {
        return halfMoveCounter;
    }

    /**
     * Sets half move counter.
     *
     * @param halfMoveCounter the halfMoveCounter to set
     */
    public void setHalfMoveCounter(Integer halfMoveCounter) {
        this.halfMoveCounter = halfMoveCounter;
    }

    /**
     * Gets castle right.
     *
     * @param side the side
     * @return the castleRight
     */
    public CastleRight getCastleRight(Side side) {
        return castleRight.get(side);
    }

    /**
     * Gets castle right.
     *
     * @return the castleRight
     */
    public EnumMap<Side, CastleRight> getCastleRight() {
        return castleRight;
    }

    /**
     * Gets context.
     *
     * @return the context
     */
    public GameContext getContext() {
        return context;
    }

    /**
     * Sets context.
     *
     * @param context the context to set
     */
    public void setContext(GameContext context) {
        this.context = context;
    }

    /**
     * Gets backup.
     *
     * @return the backup
     */
    public LinkedList<MoveBackup> getBackup() {
        return backup;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 7; i >= 0; i--) {
            Rank r = Rank.values()[i];
            for (int n = 0; n <= 7; n++) {
                File f = File.values()[n];
                if (!File.NONE.equals(f) && !Rank.NONE.equals(r)) {
                    Square sq = Square.encode(r, f);
                    Piece piece = getPiece(sq);
                    if (Piece.NONE.equals(piece)) {
                        sb.append(" ");
                    } else {
                        sb.append(Constants.getPieceNotation(piece));
                    }
                }
            }
            sb.append("\n");
        }
        sb.append("Side: " + getSideToMove());

        return sb.toString();
    }

    /**
     * Board to array piece [ ].
     *
     * @return the piece [ ]
     */
    public Piece[] boardToArray() {

        final Piece pieces[] = new Piece[65];
        pieces[64] = Piece.NONE;

        for (Square square : Square.values()) {
            if (!Square.NONE.equals(square)) {
                pieces[square.ordinal()] = getPiece(square);
            }
        }

        return pieces;
    }

    public BoardEventType getType() {
        return BoardEventType.ON_LOAD;
    }

    /**
     * Gets event listener.
     *
     * @return the event listener
     */
    public EnumMap<BoardEventType, List<BoardEventListener>> getEventListener() {
        return eventListener;
    }

    /**
     * Adds a Board Event Listener
     *
     * @param eventType the event type
     * @param listener  the listener
     * @return Board board
     */
    public Board addEventListener(BoardEventType eventType, BoardEventListener listener) {
        getEventListener().get(eventType).add(listener);
        return this;
    }

    /**
     * Removes a Board Event Listener
     *
     * @param eventType the event type
     * @param listener  the listener
     * @return Board board
     */
    public Board removeEventListener(BoardEventType eventType, BoardEventListener listener) {
        if (getEventListener() != null && getEventListener().get(eventType) != null) {
            getEventListener().get(eventType).remove(listener);
        }
        return this;
    }

    /**
     * Returns if the the bitboard with pieces which can attack the given square
     *
     * @param square the square
     * @param side   the side
     * @return true if the square is attacked
     */
    public long squareAttackedBy(Square square, Side side) {
        long result;
        long occ = getBitboard();
        result = Bitboard.getPawnAttacks(side.flip(), square) &
                getBitboard(Piece.make(side, PieceType.PAWN));
        result |= Bitboard.getKnightAttacks(square, occ) &
                getBitboard(Piece.make(side, PieceType.KNIGHT));
        result |= Bitboard.getBishopAttacks(occ, square) &
                ((getBitboard(Piece.make(side, PieceType.BISHOP)) |
                        getBitboard(Piece.make(side, PieceType.QUEEN))));
        result |= Bitboard.getRookAttacks(occ, square) &
                ((getBitboard(Piece.make(side, PieceType.ROOK)) |
                        getBitboard(Piece.make(side, PieceType.QUEEN))));
        result |= Bitboard.getKingAttacks(square, occ) &
                getBitboard(Piece.make(side, PieceType.KING));
        return result;
    }

    /**
     * Square attacked by a given piece type and side
     *
     * @param square the square
     * @param side   the side
     * @param type   the type
     * @return long
     */
    public long squareAttackedByPieceType(Square square,
                                          Side side, PieceType type) {
        long result = 0L;
        long occ = getBitboard();
        switch (type) {
            case PAWN:
                result = Bitboard.getPawnAttacks(side.flip(), square) &
                        getBitboard(Piece.make(side, PieceType.PAWN));
                break;
            case KNIGHT:
                result = Bitboard.getKnightAttacks(square, occ) &
                        getBitboard(Piece.make(side, PieceType.KNIGHT));
                break;
            case BISHOP:
                result = Bitboard.getBishopAttacks(occ, square) &
                        getBitboard(Piece.make(side, PieceType.BISHOP));
                break;
            case ROOK:
                result = Bitboard.getRookAttacks(occ, square) &
                        getBitboard(Piece.make(side, PieceType.ROOK));
                break;
            case QUEEN:
                result = Bitboard.getQueenAttacks(occ, square) &
                        getBitboard(Piece.make(side, PieceType.QUEEN));
                break;
            case KING:
                result |= Bitboard.getKingAttacks(square, occ) &
                        getBitboard(Piece.make(side, PieceType.KING));
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * Get king Square
     *
     * @param side the side
     * @return king square
     */
    public Square getKingSquare(Side side) {
        Square result = Square.NONE;
        List<Square> sq = getPieceLocation(Piece.make(side, PieceType.KING));
        if (sq != null && sq.size() > 0) {
            result = sq.get(0);
        }
        return result;
    }

    /**
     * Is King Attacked
     *
     * @return boolean
     */
    public boolean isKingAttacked() {
        return squareAttackedBy(getKingSquare(getSideToMove()), getSideToMove().flip()) != 0;
    }

    /**
     * set of squares attacked by
     *
     * @param squares the squares
     * @param side    the side
     * @return boolean
     */
    public boolean isSquareAttackedBy(List<Square> squares, Side side) {
        for (Square sq : squares) {
            if (squareAttackedBy(sq, side) != 0L) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verify if a move is legal within this board context
     *
     * @param move           the move
     * @param fullValidation the full validation
     * @return the boolean
     */
    public boolean isMoveLegal(Move move, boolean fullValidation) {

        final Piece fromPiece = getPiece(move.getFrom());
        final Piece toPiece = getPiece(move.getTo());
        final Side side = getSideToMove();
        final PieceType fromType = fromPiece.getPieceType();
        final Piece capturedPiece = getPiece(move.getTo());

        if (fullValidation) {
            if (Piece.NONE.equals(fromPiece)) {
                throw new RuntimeException("From piece cannot be null");
            }

            if (fromPiece.getPieceSide().equals(capturedPiece.getPieceSide())) {
                return false;
            }

            if (!side.equals(fromPiece.getPieceSide())) {
                return false;
            }

            boolean pawnPromoting = fromPiece.getPieceType().equals(PieceType.PAWN) &&
                    isPromoRank(side, move);
            boolean hasPromoPiece = !move.getPromotion().equals(Piece.NONE);

            if (hasPromoPiece != pawnPromoting) {
                return false;
            }
            if (fromType.equals(PieceType.KING)) {
                if (getContext().isKingSideCastle(move)) {
                    if (getCastleRight(side).equals(CastleRight.KING_AND_QUEEN_SIDE) ||
                            (getCastleRight(side).equals(CastleRight.KING_SIDE))) {
                        if ((getBitboard() & getContext().getooAllSquaresBb(side)) == 0L) {
                            return !isSquareAttackedBy(getContext().getooSquares(side), side.flip());
                        }
                    }
                    return false;
                }
                if (getContext().isQueenSideCastle(move)) {
                    if (getCastleRight(side).equals(CastleRight.KING_AND_QUEEN_SIDE) ||
                            (getCastleRight(side).equals(CastleRight.QUEEN_SIDE))) {
                        if ((getBitboard() & getContext().getoooAllSquaresBb(side)) == 0L) {
                            return !isSquareAttackedBy(getContext().getoooSquares(side), side.flip());
                        }
                    }
                    return false;
                }
            }
        }

        if (fromType.equals(PieceType.KING)) {
            if (squareAttackedBy(move.getTo(), side.flip()) != 0L) {
                return false;
            }
        }
        Square kingSq = (fromType.equals(PieceType.KING) ?
                move.getTo() : getKingSquare(side));
        Side other = side.flip();
        long moveTo = move.getTo().getBitboard();
        long moveFrom = move.getFrom().getBitboard();
        long ep = getEnPassantTarget() != Square.NONE && move.getTo() == getEnPassant() &&
                (fromType.equals(PieceType.PAWN)) ? getEnPassantTarget().getBitboard() : 0;
        long allPieces = (getBitboard() ^ moveFrom) | moveTo;

        long bishopAndQueens = ((getBitboard(Piece.make(other, PieceType.BISHOP)) |
                getBitboard(Piece.make(other, PieceType.QUEEN)))) & ~moveTo;

        if (bishopAndQueens != 0L &&
                (Bitboard.getBishopAttacks(allPieces, kingSq) & bishopAndQueens) != 0L) {
            return false;
        }

        long rookAndQueens = ((getBitboard(Piece.make(other, PieceType.ROOK)) |
                getBitboard(Piece.make(other, PieceType.QUEEN)))) & ~moveTo;

        if (rookAndQueens != 0L &&
                (Bitboard.getRookAttacks(allPieces, kingSq) & rookAndQueens) != 0L) {
            return false;
        }

        long knights = (getBitboard(Piece.make(other, PieceType.KNIGHT))) & ~moveTo;

        if (knights != 0L &&
                (Bitboard.getKnightAttacks(kingSq, allPieces) & knights) != 0L) {
            return false;
        }

        long pawns = (getBitboard(Piece.make(other, PieceType.PAWN))) & ~moveTo & ~ep;

        return pawns == 0L ||
                (Bitboard.getPawnAttacks(side, kingSq) & pawns) == 0L;
    }

    /**
     * Is attacked by boolean.
     *
     * @param move the move
     * @return the boolean
     */
    public boolean isAttackedBy(Move move) {

        PieceType pieceType = getPiece(move.getFrom()).getPieceType();
        assert (!PieceType.NONE.equals(pieceType));
        Side side = getSideToMove();
        long attacks = 0L;
        switch (pieceType) {
            case PAWN:
                if (!move.getFrom().getFile().equals(move.getTo().getFile())) {
                    attacks = Bitboard.getPawnCaptures(side, move.getFrom(),
                            getBitboard(), getEnPassantTarget());
                } else {
                    attacks = Bitboard.getPawnMoves(side, move.getFrom(), getBitboard());
                }
                break;
            case KNIGHT:
                attacks = Bitboard.getKnightAttacks(move.getFrom(), ~getBitboard(side));
                break;
            case BISHOP:
                attacks = Bitboard.getBishopAttacks(getBitboard(), move.getFrom());
                break;
            case ROOK:
                attacks = Bitboard.getRookAttacks(getBitboard(), move.getFrom());
                break;
            case QUEEN:
                attacks = Bitboard.getQueenAttacks(getBitboard(), move.getFrom());
                break;
            case KING:
                attacks = Bitboard.getKingAttacks(move.getFrom(), ~getBitboard(side));
                break;
            default:
                break;
        }
        return (attacks & move.getTo().getBitboard()) != 0L;
    }

    /**
     * Gets history.
     *
     * @return the history
     */
    public LinkedList<Integer> getHistory() {
        return history;
    }

    /**
     * Is side mated
     *
     * @return boolean
     */
    public boolean isMated() {
        try {
            if (isKingAttacked()) {
                final MoveList l = MoveGenerator.generateLegalMoves(this);
                if (l.size() == 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    /**
     * verify draw by 50th move rule, 3 fold rep and insuficient material
     *
     * @return the boolean
     */
    public boolean isDraw() {
        final int i = Math.min(getHistory().size() - 1, getHalfMoveCounter());
        int rept = 0;
        if (getHistory().size() >= 4) {
            final int lastKey = getHistory().get(getHistory().size() - 1);
            for (int x = 2; x <= i; x += 2) {
                final int k = getHistory().get(getHistory().size() - x - 1);
                if (k == lastKey) {
                    rept++;
                    if (rept >= 2) {
                        return true;
                    }
                }
            }
        }
        if (isInsufficientMaterial()) {
            return true;
        }
        if (isStaleMate()) {
            return true;
        }
        return getHalfMoveCounter() >= 100;

    }

    /**
     * Verify if there is enough material left in the board
     *
     * @return boolean
     */
    public boolean isInsufficientMaterial() {
        boolean result = false;
        final long pawns = getBitboard(Piece.WHITE_PAWN) |
                getBitboard(Piece.BLACK_PAWN);
        if (pawns == 0L) {
            if ((getBitboard(Piece.WHITE_QUEEN) +
                    getBitboard(Piece.BLACK_QUEEN) +
                    getBitboard(Piece.WHITE_ROOK) +
                    getBitboard(Piece.BLACK_ROOK)) != 0L) {
                result = false;
            } else {
                long count = Long.bitCount(getBitboard());
                if (count == 4) {
                    if (Long.bitCount(getBitboard(Side.WHITE)) > 1 &&
                            Long.bitCount(getBitboard(Side.BLACK)) > 1) {
                        result = true;
                    } else if (Long.bitCount(getBitboard(Piece.WHITE_KNIGHT)) == 2 ||
                            Long.bitCount(getBitboard(Piece.BLACK_KNIGHT)) == 2) {
                        result = true;
                    }
                } else if (count < 4) {
                    return true;
                }
            }
        }

        return result;
    }

    /**
     * Is stale mate
     *
     * @return boolean
     */
    public boolean isStaleMate() {
        try {
            if (!isKingAttacked()) {
                MoveList l = MoveGenerator.generateLegalMoves(this);
                if (l.size() == 0) {
                    return true;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    /**
     * Is enable events boolean.
     *
     * @return the enableEvents
     */
    public boolean isEnableEvents() {
        return enableEvents;
    }

    /**
     * Sets enable events.
     *
     * @param enableEvents the enableEvents to set
     */
    public void setEnableEvents(boolean enableEvents) {
        this.enableEvents = enableEvents;
    }

    /**
     * Get the unique position ID for the board state,
     * which is the actual FEN representation of the board without counters
     *
     * @return position id
     */
    public String getPositionId() {
        return getFen(false);
    }

    /**
     * (non-Javadoc)
     *
     * @see Object#equals(Object)
     */
    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj != null && obj instanceof Board) {

            Board board = (Board) obj;
            result = true;
            for (Square sq : Square.values()) {
                if (!getPiece(sq).equals(board.getPiece(sq))) {
                    result = false;
                    break;
                }
            }
            if (result) {
                result = getSideToMove().equals(board.getSideToMove());
                result = result || getCastleRight(Side.WHITE).equals(board.getCastleRight(Side.WHITE));
                result = result || getCastleRight(Side.BLACK).equals(board.getCastleRight(Side.BLACK));
                result = result || getEnPassant().equals(board.getEnPassant());
            }
        }
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = 0;
        for (Square sq : Square.values()) {
            Piece piece = getPiece(sq);
            if (!Piece.NONE.equals(piece) &&
                    !Square.NONE.equals(sq)) {
                //hash ^= sq.hashCode() ^ piece.hashCode();
                hash ^= shortKeys[11 * piece.ordinal() + sq.ordinal() ];
            }
        }
        hash ^= getSideToMove().hashCode();
        hash ^= getCastleRight(Side.WHITE).hashCode();
        hash ^= getCastleRight(Side.BLACK).hashCode();
        if (!Square.NONE.equals(getEnPassant())) {
            hash ^= getEnPassant().hashCode();
        }
        //TODO use increamental zobrist's hashing
        return hash;
    }

    @Override
    public Board clone() {
        Board copy = new Board(getContext(), this.updateHistory);
        copy.loadFromFen(this.getFen());
        return copy;
    }


}
