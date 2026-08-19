import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { BuyButton } from "@/components/buy-button";
import type { Show } from "@/lib/types";

const priceFormatter = new Intl.NumberFormat("pt-BR", {
  style: "currency",
  currency: "BRL",
});

export function ShowCard({ show }: { show: Show }) {
  const soldOut = show.availableTickets <= 0;

  return (
    <Card className="flex flex-col">
      <CardHeader className="flex flex-row items-start justify-between gap-2">
        <CardTitle>{show.name}</CardTitle>
        <Badge variant={soldOut ? "destructive" : "secondary"}>
          {soldOut ? "Esgotado" : `${show.availableTickets} restantes`}
        </Badge>
      </CardHeader>
      <CardContent className="flex-1 space-y-3">
        <p className="text-sm text-muted-foreground">{show.description}</p>
        <p className="text-lg font-semibold">{priceFormatter.format(show.price)}</p>
      </CardContent>
      <CardFooter>
        <BuyButton showId={show.id} soldOut={soldOut} />
      </CardFooter>
    </Card>
  );
}
