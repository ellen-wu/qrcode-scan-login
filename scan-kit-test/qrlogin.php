<?php

session_start();

// web端代码

include 'functions.php';
include 'phpqrcode/phpqrcode.php';


// 查看二维码
if ($_SERVER['REQUEST_METHOD'] == "GET" && $_GET['type'] == "show_qrcode") {
    // ./qrlogin.php?type=show_qrcode&code=123
    $qrcode = urldecode($_GET['code']);
    if (false !== stripos($qrcode, "action=scankit&code=")) {
        QRcode::png($qrcode, false, 'L', 8, 1);
    }
}

// 请求生成二维码
if ($_SERVER['REQUEST_METHOD'] == "POST" && $_POST['type'] == "make_qrcode") {
    if (!isset($_SESSION["login_shuffer_string"])) {
        _jsonOut(500, "亲，参数错误");
    }
    
    // 二维码5分钟有效
    $expireTime = 300;

    $timeNow = time();
    // 还有20秒过期 则可刷新
    if (!isset($_SESSION['qrcode_login_expire']) || ($timeNow > $_SESSION["qrcode_login_expire"] - 20)) {
        $ip = getIp();
        $shuffleStr = mt_rand(1000, 99999999);

        $cacheId = md5(md5($ip . $timeNow) . $shuffleStr);

        $category = "scankit";

        $param = "action=" . $category . "&code=" . $cacheId;


        // 默认缓存为 0  扫码后缓存为用户id
        cacheSave($cacheId, 0, $expireTime);

        $_SESSION["qrcode_login_param"] = $param;
        $_SESSION["qrcode_login_cacheid"] = $cacheId;
        $_SESSION["qrcode_login_expire"] = $timeNow + $expireTime;
    } else {
        $cacheId = $_SESSION["qrcode_login_cacheid"];
        $param = $_SESSION["qrcode_login_param"];
    }

    _jsonOut(200, "参数获取成功", [
        'param' => $param,
        'check_id' => $cacheId,
        'url' => './qrlogin.php?type=show_qrcode&code=' .urlencode($param)
    ]);
}

// 轮询的检查，app扫码后是否授权
if ($_SERVER['REQUEST_METHOD'] == "POST" && $_POST['type'] == "check_login") {
    if (!isset($_SESSION["login_shuffer_string"])) {
        _jsonOut(500, "亲，请刷新浏览器后，再重试");
    }

    $cacheId = $_POST["check_id"];
    
    if ($cacheId == '') {
        _jsonOut(500, "亲，请刷新浏览器后，再重试");
    }
    
    $userId = cacheGet($cacheId);
    if (empty($userId) && $userId !== "0") {
        _jsonOut(-202, "亲，二维码已过期，请刷新页面！");
    }

    // 已扫码登录
    $userId = intval($userId);
    if ($userId > 0) {
        // 删除缓存信息
        cacheDel($cacheId);
        // 这里只是模拟，实际生产中，根据实际情况进行登录后的操作
        _jsonOut(200, "亲，登录成功", ['user_id' => $userId]);
    }

    _jsonOut(1, "");
}
