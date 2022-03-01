import React, { useEffect } from 'react';
import './App.css';
import { Client } from '@stomp/stompjs';

const App = () => {

  useEffect(() => {
    const webSocketUrl =
      process.env.NODE_ENV === 'development'
        ? 'ws://localhost:9000/ws'
        : `wss://${window.location.hostname}/ws`;

    const client = new Client({
      brokerURL: webSocketUrl,
      debug: function (str) {
        console.log('WS debug: ', str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    client.onConnect = () => {
      console.log("OK")
      client.subscribe('/app/games', (games) => {
        console.log(JSON.parse(games.body), 'receivedGames');
      });
      client.publish(
        {
          destination: '/app/getGames',
          body: JSON.stringify('games')
        }
      );
    };

    client.onDisconnect = () => {
      console.log("NIE OK")
    };

    client.onStompError = (frame) => {
      console.error('WS: Broker reported error: ' + frame.headers['message']);
      console.error('WS: Additional details: ' + frame.body);
    };

    client.activate();

  }, []);


  return (
    <div>

    </div>
  );
}

export default App;
