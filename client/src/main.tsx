import { createRoot } from 'react-dom/client'
import App from './App.tsx'
import '@/index.css'
import * as Sentry from "@sentry/react"
import { ErrorFallback } from './common/component/ErrorFallback.tsx'

Sentry.init({
  dsn: "https://1c4ee66ce3dc76b043dc68e9f3e7f419@o4511846719684608.ingest.us.sentry.io/4511846749962240",
});
createRoot(document.getElementById('root')!).render(
  // <StrictMode>
  <Sentry.ErrorBoundary fallback={<ErrorFallback />}>
    <App />
  </Sentry.ErrorBoundary>
  //</StrictMode>
)
