package com.team573.gongguri.domain.chat.config

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig(
    private val chatSubscriptionInterceptor: ChatSubscriptionInterceptor
) : WebSocketMessageBrokerConfigurer {

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.setApplicationDestinationPrefixes("/send") // 클라이언트 -> 서버
        registry.enableSimpleBroker("/room") // 서버 -> 클라이언트
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/stomp") //SockJS 연결 주소
            .withSockJS()
        // 주소 : ws://localhost:8080/stomp/chat
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(chatSubscriptionInterceptor) // 여기에서 주입됨
    }
}
