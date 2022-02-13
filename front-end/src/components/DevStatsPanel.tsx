type DevStatsPanelProps = {
  inputDelta: string;
  input: string;
  lhsEquiv: string;
  rhsEquiv: string;
};

function DevStatsPanel({
  inputDelta,
  input,
  lhsEquiv,
  rhsEquiv,
}: DevStatsPanelProps) {
  return (
    <div id="apiStatsPanel">
      <h2>Dev Stats</h2>
      <span className="boldText">New input:</span> {inputDelta}
      <br />
      <br />
      <span className="boldText">Input: </span>
      {input}
      <br />
      <span className="boldText">Left-hand equivalent: </span>
      {lhsEquiv}
      <br />
      <span className="boldText">Right-hand equivalent: </span>
      {rhsEquiv}
    </div>
  );
}

export default DevStatsPanel;
