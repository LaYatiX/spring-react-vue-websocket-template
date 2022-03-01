package pl.gpiwosz.tictactoe.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    config.enableSimpleBroker("/app");
    config.setApplicationDestinationPrefixes("/app");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry
      .addEndpoint("/ws")
      .setAllowedOrigins("*")
      .setHandshakeHandler(new CustomHandshakeHandler());

    registry
      .addEndpoint("/ws")
      .setAllowedOrigins("*")
      .setHandshakeHandler(new CustomHandshakeHandler())
      .withSockJS();
  }


//  @Override
//  public void registerStompEndpoints(StompEndpointRegistry registry) {
//    registry.addEndpoint("/chat");
//    registry.addEndpoint("/chat").withSockJS();
//    registry
//      .addEndpoint("/greeting")
//      .setHandshakeHandler(new DefaultHandshakeHandler() {
//        @Override
//        protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
//          return super.determineUser(request, wsHandler, attributes);
//        }
//        public boolean beforeHandshake(
//          ServerHttpRequest request,
//          ServerHttpResponse response,
//          WebSocketHandler wsHandler,
//          Map attributes) throws Exception {
//
//          if (request instanceof ServletServerHttpRequest) {
//            ServletServerHttpRequest servletRequest
//              = (ServletServerHttpRequest) request;
//            HttpSession session = servletRequest
//              .getServletRequest().getSession();
//            attributes.put("sessionId", session.getId());
//          }
//          return true;
//        }
//      }).withSockJS();
//  }
}
