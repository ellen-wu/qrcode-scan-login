<?php

session_start();
session_destroy();

session_start();
// 登录页面


// 会话中保存一个字符串 用于生成二维码
$loginShufferString = mt_rand(1000, 9999999);

$_SESSION["login_shuffer_string"] = $loginShufferString;


include("login.html");
