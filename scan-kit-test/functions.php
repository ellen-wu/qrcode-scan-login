<?php

// 一些公用函数

/**
 * 输出json
 * @param  integer $code [description]
 * @param  string  $msg  [description]
 * @param  array   $data [description]
 * @return [type]        [description]
 */
function _jsonOut($code = 200, $msg = '', $data = [])
{
    $jsonArray = [
        'code' => $code,
        'msg' => $msg,
        'data' => $data
    ];

    header('Content-type: application/json;chartset=uft-8');
    echo json_encode($jsonArray);
    die();
}

/**
 * 获取客户端ip
 * @param  integer $type [description]
 * @return [type]        [description]
 */
function getIp($type = 0)
{
    if (isset($_SERVER['HTTP_X_FORWARDED_FOR'])) {
        $arr = explode(',', $_SERVER['HTTP_X_FORWARDED_FOR']);
        $pos = array_search('unknown', $arr);
        if (false !== $pos) {
            unset($arr[$pos]);
        }
        $ip = trim(current($arr));
    } elseif (isset($_SERVER['HTTP_CLIENT_IP'])) {
        $ip = $_SERVER['HTTP_CLIENT_IP'];
    } elseif (isset($_SERVER['REMOTE_ADDR'])) {
        $ip = $_SERVER['REMOTE_ADDR'];
    }

    // IP地址合法验证
    $long = sprintf("%u", ip2long($ip));
    $ip   = $long ? [$ip, $long] : ['0.0.0.0', 0];
    return $ip[$type];
}

/**
 * 缓存到redis
 * @param  [type] $cacheId    [description]
 * @param  [type] $value      [description]
 * @param  [type] $expireTime [description]
 * @return [type]             [description]
 */
function cacheSave($cacheId, $value, $expireTime)
{
    $redis = new Redis();
    $redis->pconnect("127.0.0.1", 6379);

    $redis->set($cacheId, $value, $expireTime);
}

/**
 * 获取缓存
 * @param  [type] $cacheId [description]
 * @return [type]          [description]
 */
function cacheGet($cacheId)
{
    $redis = new Redis();
    $redis->pconnect("127.0.0.1", 6379);

    if (!$redis->exists($cacheId)) {
        return false;
    }
    return $redis->get($cacheId);
}

/**
 * cache是否存在
 * @param  [type] $cacheId [description]
 * @return [type]          [description]
 */
function cacheIsExists($cacheId)
{
    $redis = new Redis();
    $redis->pconnect("127.0.0.1", 6379);

    return $redis->exists($cacheId);
}

/**
 * 删除缓存
 * @param  [type] $cacheId [description]
 * @return [type]          [description]
 */
function cacheDel($cacheId)
{
    $redis = new Redis();
    $redis->pconnect("127.0.0.1", 6379);

    $redis->del($cacheId);
}
