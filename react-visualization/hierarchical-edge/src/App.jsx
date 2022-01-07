import { useState } from 'react'
import { graphGenerator } from './utils'
import './App.css'

function App() {
  const [shouldDisplayFlare, setShouldDisplayFlare] = useState(false)

  const handleFileAdd = ({ target: { files } }) => {
    files[0].text()
      .then((res) => {
        setShouldDisplayFlare(true)
        setTimeout(() => {
          graphGenerator(JSON.parse(res))
        })
      })
      .catch((error) => {
        console.error(error)
        alert('meh')
      })
  }

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
    </div>
  )
}

export default App
