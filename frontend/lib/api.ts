import axios from "axios";

// Used from Server Components and Route Handlers — inside Docker this must
// resolve via the compose network (GATEWAY_INTERNAL_URL), since "localhost"
// there means the frontend container itself, not the gateway.
export const serverApi = axios.create({
  baseURL:
    process.env.GATEWAY_INTERNAL_URL ??
    process.env.NEXT_PUBLIC_GATEWAY_URL ??
    "http://localhost:8080",
});
