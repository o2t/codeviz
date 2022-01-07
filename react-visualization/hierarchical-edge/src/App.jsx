import { useEffect, useState } from 'react'
import { graphGenerator } from './utils'
import './App.css'

function App() {
  const [shouldDisplayFlare, setShouldDisplayFlare] = useState(false)
  const [currentNode, setCurrentNode] = useState('')
  const [mousePosition, setMousePosition] = useState({ x: 0, y: 0})

  const handleFileAdd = ({ target: { files } }) => {
    files[0].text()
      .then((res) => {
        setShouldDisplayFlare(true)
        setTimeout(() => {
          graphGenerator(JSON.parse(res), setCurrentNode)
        })
      })
      .catch((error) => {
        console.error(error)
        alert('meh')
      })
  }

  const handleMouseMove = (e) => {
    setMousePosition({
      x: e.pageX + 80 + 'px',
      y: e.pageY + 80 + 'px',
    })
  }

  useEffect(() => {
    document.addEventListener('mousemove', handleMouseMove);

    return () => {
      document.removeEventListener('mousemove', handleMouseMove);
    }
  }, [])

  if (!shouldDisplayFlare) {
    return (
      <div className='no-file'>
        <label>Please select a file</label>
        <input className='file-selector' type="file" onChange={handleFileAdd} />
      </div>
    )
  }

  return (
    <div className="App">
      <div className='current-node' style={{ left: mousePosition.x, top: mousePosition.y }}>{currentNode}</div>
    </div>
  )
}

export default App
