package com.example.proximity

/**
 * normal server using blocking IO means always waiting client's
 * request then starting communication
 * to improve 1 NIO(more suitable for big project??) 低负载、低并发的应用程序可以选择同步阻塞 I/O 以降低编程复杂度
 * 2 Rxjava (easy to understand event drive)or
 * kotlin coroutines (consume less resource) response time?
 *First using RXjava(5.31)
 */
class ServerSocket{

}