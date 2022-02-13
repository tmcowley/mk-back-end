import { useState } from "react";

import axios, { AxiosRequestConfig, AxiosResponse } from "axios";

import ReactDOM from "react-dom";

import { useEffect } from "react";

// import local components
import DevStatsPanel from "./components/DevStatsPanel";
import PromptText from "./components/PromptText";
import Header from "./components/Header";

// import logo from './logo.svg';

import "./App.css";

function App() {
  // text input and input delta (added characters)
  const [input, setInput] = useState("");
  const [inputDelta, setInputDelta] = useState("");

  // left and right hand side interpretations of input
  const [lhsEquiv, setLhsEquiv] = useState("");
  const [rhsEquiv, setRhsEquiv] = useState("");

  // stores computed success state
  const [computed, setComputed] = useState(false);

  // stores API active state
  const [apiActive, setApiActive] = useState(false);

  // API & Axios config
  const axiosConfig: AxiosRequestConfig<string> = {
    headers: {
      // 'Content-Length': 0,
      "Content-Type": "text/plain",
    },
    responseType: "json",
  };
  const host = "http://localhost:8080";

  function queryAPIStatus() {
    const path = "/api/status";
    const url = host + path;
    const data = null;

    axios.post(url, data, axiosConfig).then(
      (response) => {
        console.log("queryAPIStatus() - Response found");
        console.log(response);

        setApiActive(true);
      },
      (error) => {
        console.log("queryAPIStatus() - error found");
        console.log(error);

        setApiActive(false);
      }
    );
  }

  // on page load
  useEffect(() => {
    // query API active state
    queryAPIStatus();

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // on update of input => update stats, results
  useEffect(() => {
    // calculate and render LHS and RHS interpretations
    renderEquivalents(input);

    // launch post request to get matching sentences
    postInput(input);

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [input]);

  // when the API becomes active
  useEffect(() => {
    // block inactive api state
    if (!apiActive) {
      return;
    }

    // focus-on and select input box
    const inputElement: HTMLInputElement = document.getElementById(
      "input"
    )! as HTMLInputElement;
    inputElement.focus();
    inputElement.select();
  }, [apiActive]);

  // when the API becomes active
  useEffect(() => {
    // block active api state
    if (apiActive) {
      return;
    }

    // query API status every second
    const interval = setInterval(() => {
      queryAPIStatus();
    }, 1000);
    return () => clearInterval(interval);

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [apiActive]);

  return (
    <body className="App">
      <Header />

      <br />
      <hr />

      <PromptText />

      <br />
      <hr />

      <div id="content">
        <form onSubmit={(e) => handleFormSubmit(e)} autoComplete="off">
          <label>
            <input
              id="input"
              type="text"
              value={input}
              onInput={(e) => handleOnInput(e)}
              onFocus={(_) => queryAPIStatus()}
              disabled={!apiActive}
            />
          </label>
        </form>

        <br />
        <hr />

        <div id="resultsContainer" hidden={input === "" || !computed}>
          <h2>Results</h2>
          <div className="grid-container">
            <div className="grid-child">
              <h3>Sentence-based algorithm (1)</h3>
              <ul id="results"></ul>
            </div>

            <div className="grid-child">
              <h3>Real-time algorithm (2)</h3>
              ...
            </div>
          </div>
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

  function handleOnInput(event: React.FormEvent<HTMLInputElement>) {
    queryAPIStatus();

    const oldValue = input;
    const inputElement = event.target as HTMLInputElement;
    const newValue = inputElement.value;
    const selectionEnd = inputElement.selectionEnd!;

    // update the state-stored input
    setInput(newValue);

    // calculate the input delta (new characters)
    const inputDelta = getStringDelta(oldValue, newValue, selectionEnd);
    setInputDelta(inputDelta);
  }

  function renderEquivalents(input: string) {
    if (!input || input === "") {
      setLhsEquiv("");
      setRhsEquiv("");
    }

    // calculate rhs interpretation
    var path = "/api/convert/lhs";
    var url = host + path;

    const data = input;

    axios.post(url, data, axiosConfig).then(
      (response) => {
        // console.log(response);

        const lhsEquiv = response.data;
        setLhsEquiv(lhsEquiv);
      },
      (error) => {
        console.log(error);
        queryAPIStatus();
      }
    );

    path = "/api/convert/rhs";
    url = host + path;

    axios.post(url, data, axiosConfig).then(
      (response) => {
        // console.log(response);

        const rhsEquiv = response.data;
        setRhsEquiv(rhsEquiv);
      },
      (error) => {
        console.log(error);
        queryAPIStatus();
      }
    );
  }

  function handleFormSubmit(event: React.FormEvent) {
    // prevent default form submission
    event.preventDefault();

    // // get input text
    // const input = event.target[0].value;

    // // launch post request to back-end
    // // to get matching sentences
    // postInput(input);
  }

  function postInput(input: string) {
    if (!input || input === "") {
      setComputed(false);
      return;
    }

    const path = "/api/submit";
    const url = host + path;

    const data = input;

    axios.post(url, data, axiosConfig).then(
      (response) => {
        // render results from response
        renderResults(response);
        setComputed(true);
      },
      (error) => {
        console.log(error);
        setComputed(false);
        queryAPIStatus();
      }
    );
  }

  function renderResults(response: AxiosResponse) {
    // generate the results list
    const resultsArray = response.data;
    let results = resultsArray.map((item: string, i: number) => {
      return <li key={i}>{item}</li>;
    });

    // render the results list
    ReactDOM.render(results, document.getElementById("results"));
  }

  // Developed with help from https://stackoverflow.com/a/34217353
  function getStringDelta(
    oldString: string,
    newString: string,
    selEnd: number
  ) {
    const textLost: boolean = newString.length < oldString.length;
    if (textLost) {
      console.log("Notice: User has removed or cut character(s)");
      return "";
    }

    const deltaSize: number = newString.length - oldString.length;
    const selStart: number = selEnd - deltaSize;

    const isAppend: boolean = newString.substring(0, selStart) === oldString;

    if (isAppend) {
      return newString.substring(selStart, selEnd);
    } else {
      console.log("Notice: User has overwritten content");
      return "";
    }
  }
}

export default App;
