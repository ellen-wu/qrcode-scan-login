<?php

// app端接口
// 实际上app端的，token可能是双向加密的，可以解密与用户id进行匹配
// 
include 'functions.php';

if ($_SERVER['REQUEST_METHOD'] == "POST") {
    if (isset($_POST['user_id']) && isset($_POST['token']) && isset($_POST['qrcode_string'])) {
        if ($_POST['user_id'] != 100) {
            _jsonOut(500, '亲，用户id有误');
        }
        if ($_POST['token'] != "this is used to test!") {
            _jsonOut(500, '亲，用户id有误');
        }
        if (false === stripos($_POST['qrcode_string'], "action=scankit&code=")) {
            _jsonOut(500, '亲，请使用正确的登录二维码');
        }
        // 正确后 修改redis中缓存的key为user_id
        
        $tmp = explode('&', trim($_POST['qrcode_string']));

        $tmp = explode("=", $tmp[1]);
        $cacheId = $tmp[1];

        if (cacheIsExists($cacheId)) {
            cacheSave($cacheId, $_POST['user_id'], 30);
        } else {
            _jsonOut(500, '亲，二维码已经失效，请刷新二维码！');
        }
        _jsonOut(200, '亲，登录成功，请勿刷新浏览器！');
    } else {
        _jsonOut(500, '亲，参数错误');
    }
}

_jsonOut(500, '亲，参数错误');
