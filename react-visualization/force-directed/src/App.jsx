import { useState } from 'react'
import ForceGraph2D from 'react-force-graph-2d'
import ForceGraph3D from 'react-force-graph-3d'
import './App.css'

function App() {
  const [graph2DData, setGraph2DData] = useState(null)
  const [graph3DData, setGraph3DData] = useState(null)

  const handleFileAdd2D = ({ target: { files } }) => {
    files[0].text()
      .then((res) => {
        setGraph2DData(JSON.parse(res))
      })
      .catch((error) => {
        console.error(error)
        alert('meh')
      })
  }

  const handleFileAdd3D = ({ target: { files } }) => {
    files[0].text()
      .then((res) => {
        setGraph3DData(JSON.parse(res))
      })
      .catch((error) => {
        console.error(error)
        alert('meh')
      })
  }

  if (!graph2DData && !graph3DData) {
    return (
      <div className='no-file'>
        <div>
          <label>2D</label>
          <input className='file-selector' type="file" onChange={handleFileAdd2D} />
        </div>
        <div>
          <label>3D</label>
          <input className='file-selector' type="file" onChange={handleFileAdd3D} />
        </div>
      </div>
    )
  }

  return (
    <div className="App">
      {!!graph2DData && (
        <ForceGraph2D
          graphData={graph2DData}
          nodeLabel='id'
        />
      )}
      {!!graph3DData && (
        <ForceGraph3D
          graphData={graph3DData}
          nodeLabel='id'
        />
      )}
    </div>
  )
}

export default App
