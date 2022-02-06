import { useState } from "react";

import axios from 'axios';

import ReactDOM from 'react-dom';

import { useEffect } from "react";

// import logo from './logo.svg';
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

function DevStatsPanel({ inputDelta, input, lhsEquiv, rhsEquiv }) {
  return (
    <div id="apiStatsPanel">
      <hr />
      <h2>Dev Stats</h2>
      <span className="boldText">New input:</span> {inputDelta}
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

  // text input and input delta (added characters)
  const [input, setInput] = useState("");
  const [inputDelta, setInputDelta] = useState("");

  // left and right hand side interpretations of input
  const [lhsEquiv, setLhsEquiv] = useState("");
  const [rhsEquiv, setRhsEquiv] = useState("");

  // stores computed success state
  const [computed, setComputed] = useState(false);


  // on page load => focus and select input box
  useEffect(() => {
    var input = document.getElementById('input');
    input.focus();
    input.select();
  }, []);

  // on update of input => update stats, results
  useEffect(() => {
    // calculate and render LHS and RHS interpretations
    renderEquivalents(input);

    // launch post request to back-end
    // to get matching sentences
    postInput(input);

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [input]);

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
              onInput={
                (e) => handleOnInput(e)
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
          inputDelta={inputDelta}
          input={input}
          lhsEquiv={lhsEquiv}
          rhsEquiv={rhsEquiv}
        />

      </div>

    </body>
  );

  function handleOnInput(event) {

    const oldValue = input;
    const newValue = event.target.value;
    const selectionEnd = event.target.selectionEnd;

    // update the state-stored input
    setInput(newValue);

    // calculate the input delta (new characters)
    const inputDelta = getStringDelta(oldValue, newValue, selectionEnd);
    setInputDelta(inputDelta);
  }

  function renderEquivalents(input) {
    // calculate rhs interpretation
    const host = "http://localhost:8080";
    var path = "/api/convert/lhs";
    var url = host + path;

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

    // prevent default form submission
    event.preventDefault();

    // // get input text
    // const input = event.target[0].value;

    // // launch post request to back-end
    // // to get matching sentences
    // postInput(input);
  }

  function postInput(input) {

    if (!input || input === "") {
      setComputed(false);
      return;
    }

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

        // render results from response
        renderResults(response);
        setComputed(true);

      }, (error) => {
        console.log(error);
        setComputed(false);
      });
  }

  function renderResults(response) {

    // generate the results list
    const resultsArray = response.data;
    let results = resultsArray.map((item, i) => {
      return (
        <li key={i}>{item}</li>
      );
    });

    // render the results list
    ReactDOM.render(
      results,
      document.getElementById('results')
    );
  }

  // Developed with help from https://stackoverflow.com/a/34217353
  function getStringDelta(oldString, newString, selEnd) {
    const textLost = (newString.length < oldString.length);
    if (textLost) {
      console.log("Notice: User has removed or cut character(s)");
      return "";
    }

    const deltaSize = (newString.length - oldString.length);
    const selStart = (selEnd - deltaSize);

    const isAppend = (newString.substring(0, selStart) === oldString);

    if (isAppend) {
      return newString.substring(selStart, selEnd);
    } else {
      console.log("Notice: User has overwritten content");
      return "";
    }
  }

}

export default App;
