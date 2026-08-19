import { NextResponse } from "next/server";
import { auth0 } from "@/lib/auth0";

function decodeJwtPayload(token: string) {
  const payload = token.split(".")[1];
  const json = Buffer.from(payload, "base64url").toString("utf-8");
  return JSON.parse(json);
}

export async function GET() {
  try {
    const { token, scope, token_type, audience } = await auth0.getAccessToken();
    const parts = token.split(".");

    if (parts.length !== 3) {
      return NextResponse.json({
        isJwt: false,
        note: "O access token não é um JWT (token opaco). Isso normalmente significa que nenhum 'audience' válido foi usado no login — o Auth0Client precisa de authorizationParameters.audience apontando pra uma API registrada no dashboard da Auth0.",
        token_type,
        scope,
        audience,
        tokenPartsCount: parts.length,
      });
    }

    return NextResponse.json({ isJwt: true, claims: decodeJwtPayload(token), scope, audience });
  } catch (error) {
    return NextResponse.json({ error: String(error) }, { status: 401 });
  }
}
