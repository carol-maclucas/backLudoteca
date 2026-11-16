import { Clients } from "../../clients/model/Clients";
import { Game } from "../../game/model/Game";

export interface Loan {
    id: number;    
    game: Game;
    client: Clients;
    startDate: Date;
    endDate: Date;
}