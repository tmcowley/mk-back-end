import { useState } from "react";

import axios, { AxiosRequestConfig, AxiosResponse } from "axios";

import ReactDOM from "react-dom";

import { useEffect } from "react";

// import local components
import DevStatsPanel from "./components/DevStatsPanel";
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

  // stores text prompt
  const [prompt, setPrompt] = useState("");

  // const [resultIndex, setResultIndex] = useState(null);

  // API & Axios config
  const axiosConfig: AxiosRequestConfig<string> = {
    headers: {
      // 'Content-Length': 0,
      "Content-Type": "text/plain",
    },
    responseType: "json",
  };
  const host = "http://localhost:8080";

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

    if (prompt === "") {
      // if first time -> query text prompt
      populatePrompt();
    }

    // focus-on and select input box
    highlightInputBox();

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [apiActive]);

  // when the API becomes inactive
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

      <div id="promptText">
        <p>
          {prompt === "" ? "Prompt goes here" : prompt.replaceAll(" ", "_")}
        </p>
        <button type="button" onClick={(_) => populatePrompt()}>
          Skip
        </button>
      </div>

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
            <div id="sentenceResults" className="grid-child">
              <h3>Sentence-based algorithm (1)</h3>
              <ol id="results"></ol>
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

  function queryAPIStatus() {
    const path = "/get/status";
    const url = host + path;

    axios.get(url).then(
      (response) => {
        // console.log("queryAPIStatus() - Response found");
        // console.log(response);
        setApiActive(true);
      },
      (error) => {
        console.log("queryAPIStatus() - API endpoints down");
        // console.log(error);

        setApiActive(false);
      }
    );
  }

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
    var path = "/get/convert/lhs";
    var url = host + path;

    let config: AxiosRequestConfig<string> = axiosConfig;
    config["params"] = {
      input: input
    }

    axios.get(url, config).then(
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

    path = "/get/convert/rhs";
    url = host + path;

    axios.get(url, config).then(
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

  function handleElementSubmit(e: KeyboardEvent) {
    // listen for enter key
    if (e.key === "Enter") {
      // find current selected result
      let activeResult: HTMLLIElement = document.activeElement as HTMLLIElement;

      // let resultsDiv: HTMLDivElement = (document.getElementById("sentenceResults") as HTMLDivElement)!;
      // // get results (list elements) collection
      // let resultsList = resultsDiv.querySelector("ol");
      // let results: HTMLCollection = resultsList?.children!

      // let activeResult: (HTMLLIElement | null) = null;
      // for (let i = 0; i < results.length; i++) {
      //   let result: HTMLLIElement = results[i] as HTMLLIElement;
      //   if (result.classList.contains('focus') || result.classList.contains('active')) {
      //     // found the active result
      //     activeResult = result;
      //     break;
      //   }
      // }

      // activeResult = (document.activeElement as HTMLLIElement);

      // // no results selected by user
      // if (activeResult === null) {
      //   alert("active is null")
      //   return;
      // }

      // alert("tc: " + activeResult.textContent)

      // alert(activeResult.textContent)
      // alert(prompt)
      // alert(activeResult.textContent === prompt)

      if (activeResult.textContent === prompt) {
        console.log("success");
        populatePrompt();
      }

      // listen for numeric entry
      // ...
    }
  }

  function handleFormSubmit(event: React.FormEvent) {
    // prevent default form submission
    event.preventDefault();

    // remove highlight from text input box
    unhighlightInputBox();

    // highlight sentence based algorithm box
    let resultsDiv: HTMLDivElement = (document.getElementById(
      "sentenceResults"
    ) as HTMLDivElement)!;
    resultsDiv.style.background = "lightblue";

    // highlight first sentence in list

    // get results (list elements) collection
    let resultsList = resultsDiv.querySelector("ol");
    let results: HTMLCollection = resultsList?.children!;

    // ensure there are results
    const noResults: boolean = results.length === 0;
    if (noResults) {
      // do nothing

      // re-highlight input box
      highlightInputBox();

      return;
    }

    // add event (keydown) listener to parent div
    // resultsDiv.addEventListener("keydown", (e) => handleElementSubmit(e));
    resultsDiv.addEventListener("keydown", handleElementSubmit);

    // listen for numeric entry
    // ...

    // highlight the first result
    const firstListEl: HTMLLIElement = results[0] as HTMLLIElement;
    firstListEl.focus();

    // for (let i = 0; i < results.length; i++) {
    //   let result = results[i].textContent;
    //   console.log(result)
    // }

    // setResultIndex(0);
  }

  function postInput(input: string) {
    if (!input || input === "") {
      setComputed(false);
      return;
    }

    const path = "/get/submit";
    const url = host + path;

    // const data = input;

    let config: AxiosRequestConfig<string> = axiosConfig;
    config["params"] = {
      input: input
    }

    axios.get(url, config).then(
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
    resultsArray.reverse();
    let results = resultsArray.map((item: string, i: number) => {
      return (
        <li tabIndex={i + 200} id={"li" + i} key={i}>
          {item}
        </li>
      );
    });

    // render the results list
    ReactDOM.render(results, document.getElementById("results"));
  }

  function clearInputBox() {
    (document.getElementById("input") as HTMLInputElement).value = "";
    setInput("");
  }

  function populatePrompt() {
    // remove event listener from results div
    let resultsDiv: HTMLDivElement = (document.getElementById(
      "sentenceResults"
    ) as HTMLDivElement)!;
    resultsDiv.removeEventListener("keydown", handleElementSubmit);

    // clear input box, reset input
    clearInputBox()

    // remove highlighting from results div
    unhighlightResultsDiv();

    highlightInputBox();

    // get new prompt, populate
    const path = "/get/random-phrase";
    const url = host + path;

    axios.get(url).then(
      (response) => {
        // set prompt text
        let prompt: string = response.data;
        prompt = prompt.toLowerCase();
        setPrompt(prompt);
      },
      (error) => {
        console.log("Error");
        console.log(error);
        queryAPIStatus();
      }
    );
  }

  function unhighlightResultsDiv() {
    // remove highlighting from results div
    let resultsDiv: HTMLDivElement = (document.getElementById(
      "sentenceResults"
    ) as HTMLDivElement)!;
    resultsDiv.style.background = "";
  }

  function highlightInputBox() {
    // focus-on and select input box
    const inputElement: HTMLInputElement = document.getElementById(
      "input"
    )! as HTMLInputElement;
    inputElement.focus();
    inputElement.select();
  }

  function unhighlightInputBox() {
    // focus-on and select input box
    const inputElement: HTMLInputElement = document.getElementById(
      "input"
    )! as HTMLInputElement;
    inputElement.blur();
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
