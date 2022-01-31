import { useState } from "react";

import axios from 'axios';

import ReactDOM from 'react-dom';

import { useEffect } from "react";

import logo from './logo.svg';
import './App.css';

function Header() {
  return (
    <header className="App-header">
      <p>
        Mirrored keyboard algorithm POC
      </p>
    </header>
  );
}

function DevStatsPanel({ newInput, input, lhsEquiv, rhsEquiv }) {
  return (
    <div id="apiStatsPanel">
      <hr />
      <h2>Dev Stats</h2>
      <span className="boldText">New input:</span> {newInput}
      <br />
      <br />
      <span className="boldText">Input: </span>{input}
      <br />
      <span className="boldText">Left-hand equivalent: </span>{lhsEquiv}
      <br />
      <span className="boldText">Right-hand equivalent: </span>{rhsEquiv}
    </div>
  );
}

function App() {

  const [input, setInput] = useState("");
  const [newInput, setNewInput] = useState("");
  const [lhsEquiv, setLhsEquiv] = useState("");
  const [rhsEquiv, setRhsEquiv] = useState("");

  const [computed, setComputed] = useState(false);


  // focus and select input box on load
  useEffect(() => {
    var input = document.getElementById('input');
    input.focus();
    input.select();
  }, []);

  return (
    <body className="App">

      <Header />

      <div id="content">
        <form
          onSubmit={
            (e) => handleFormSubmit(e)
          }
        >
          <label>
            <input
              id="input"
              type="text"
              value={input}
              onChange={
                (e) => handleOnChange(e)
              }
            />
          </label>
        </form>

        <br />
        <hr />
        <div id="resultsDiv" hidden={(input === "" || !computed)}>
          <h2>Results</h2>
          <ul>
            <div id="results"></div>
          </ul>

        </div>

        <DevStatsPanel
          newInput={newInput}
          input={input}
          lhsEquiv={lhsEquiv}
          rhsEquiv={rhsEquiv}
        />

      </div>

    </body>
  );

  // https://stackoverflow.com/a/34217353
  function getInputtedString(prev, curr, selEnd) {
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

    setComputed(false);

    const oldValue = input;

    const value = event.target.value;
    const selectionEnd = event.target.selectionEnd;
    setInput(value);
    // console.log(event);

    const newInput = getInputtedString(oldValue, value, selectionEnd);
    setNewInput(newInput);

    // // branch: single character or multiple? 
    // const isCharInput = (newInput.length === 1);

    // if (isCharInput) {
    //   // launch char addition
    // }

    // else {
    //   // launch multi-char addition
    // }

    // calculate rhs interpretation
    const host = "http://localhost:8080";
    var path = "/api/convert/lhs";
    var url = host + path;

    const data = value;

    var config = {
      headers: {
        // 'Content-Length': 0,
        'Content-Type': 'text/plain'
      },
      responseType: 'json'
    };

    axios.post(url, data, config)
      .then((response) => {
        // console.log(response);

        const lhsEquiv = response.data;
        setLhsEquiv(lhsEquiv);

      }, (error) => {
        console.log(error);
      });

    path = "/api/convert/rhs";
    url = host + path;

    axios.post(url, data, config)
      .then((response) => {
        // console.log(response);

        const rhsEquiv = response.data;
        setRhsEquiv(rhsEquiv);

      }, (error) => {
        console.log(error);
      });
  }

  function handleFormSubmit(event) {

    // prevent form submission
    event.preventDefault();
    // console.log(event);

    // get input text
    const input = event.target[0].value;
    // alert(input);

    // launch post request to back-end
    // to get matching sentences

    const host = "http://localhost:8080";
    const path = "/api/submit";
    const url = host + path;

    const data = input;

    var config = {
      headers: {
        // 'Content-Length': 0,
        'Content-Type': 'text/plain'
      },
      responseType: 'json'
    };

    axios.post(url, data, config)
      .then((response) => {
        // console.log(response);

        const resultsArray = response.data;

        let results = resultsArray.map((item, i) => {
          return (
            <li key={i}>{item}</li>
          );
        });

        ReactDOM.render(
          results,
          document.getElementById('results')
        );

        setComputed(true);

      }, (error) => {
        console.log(error);
      });

  }


}

export default App;
