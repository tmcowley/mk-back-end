import { useState } from "react";

import logo from './logo.svg';
import './App.css';

function Header() {
  // return (
  //   <header className="App-header">
  //     <img src={logo} className="App-logo" alt="logo" />
  //     <p>
  //       Edit <code>src/App.js</code> and save to reload.
  //     </p>
  //     <a
  //       className="App-link"
  //       href="https://reactjs.org"
  //       target="_blank"
  //       rel="noopener noreferrer"
  //     >
  //       Learn React
  //     </a>
  //   </header>
  // );

  return (
    <header className="App-header">
      <p>
        Mirrored keyboard algorithm POC
      </p>
    </header>
  );
}

function ApiStatsPanel({ newInput }) {
  return (
    <div id="apiStatsPanel">
      New input: {newInput}
    </div>
  );
}

function App() {

  const [input, setInput] = useState("");
  const [newInput, setNewInput] = useState("");

  return (
    <div className="App">
      <body>

        <Header />

        <div id="content">
          <form>
            <label>
              <input
                type="text"
                value={input}
                onChange={
                  (e) => handleOnChange(e)}
              />
            </label>
          </form>

          <ApiStatsPanel
            newInput={newInput}
          />

        </div>

      </body>

    </div>
  );

  // https://stackoverflow.com/a/34217353
  function getInputedString(prev, curr, selEnd) {
    if (prev.length > curr.length) {
      console.log("User has removed or cut character(s)");
      return "";
    }

    var lengthOfPasted = curr.length - prev.length;
    if (curr.substr(0, selEnd - lengthOfPasted) + curr.substr(selEnd) === prev) {
      return curr.substr(selEnd - lengthOfPasted, lengthOfPasted);
    } else {
      console.log("The user has replaced a selection :(");
      return "n\\a";
    }
  }

  function handleOnChange(event) {

    const oldValue = input;

    const value = event.target.value;
    const selectionEnd = event.target.selectionEnd;
    setInput(value);
    // console.log(event);

    const newInput = getInputedString(oldValue, value, selectionEnd);
    setNewInput(newInput);

    // branch: single character or multiple? 
    const isCharInput = (newInput.length === 1);

    if (isCharInput) {
      // launch char addition
    }

    else {
      // launch multi-char addition
    }
  }


}

// function App() {
//   return (
//     <div className="App">
//       <header className="App-header">
//         <img src={logo} className="App-logo" alt="logo" />
//         <p>
//           Edit <code>src/App.js</code> and save to reload.
//         </p>
//         <a
//           className="App-link"
//           href="https://reactjs.org"
//           target="_blank"
//           rel="noopener noreferrer"
//         >
//           Learn React
//         </a>
//       </header>
//     </div>
//   );
// }

export default App;
