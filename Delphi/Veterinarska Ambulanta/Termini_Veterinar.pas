unit Termini_Veterinar;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs,
  FireDAC.Stan.Intf, FireDAC.Stan.Option, FireDAC.Stan.Param,
  FireDAC.Stan.Error, FireDAC.DatS, FireDAC.Phys.Intf, FireDAC.DApt.Intf,
  FireDAC.Stan.Async, FireDAC.DApt, Data.DB, FireDAC.Comp.DataSet,
  FireDAC.Comp.Client, FMX.Controls.Presentation, FMX.StdCtrls, FMX.Layouts,
  FMX.ListBox, System.Rtti, FMX.Grid.Style, FMX.Grid, FMX.ScrollBox, pregled,
  FMX.Objects, FMX.DateTimeCtrls;



type
  TfrmTerminiVeterinar = class(TForm)
    Spisak: TStringGrid;
    ImeLjubimca: TStringColumn;
    VrstaLjubimca: TStringColumn;
    ImeVlasnika: TStringColumn;
    Datum: TDateColumn;
    Vreme: TTimeColumn;
    Pregled: TButton;
    PrezimeVlasnika: TStringColumn;
    IDTermina: TIntegerColumn;
    Navbar: TImage;
    StyleBook: TStyleBook;
    Forma: TPanel;
    Odjava: TButton;
    Datum_Edit: TDateEdit;
    Obavljen: TStringColumn;
    Vakcinacija: TStringColumn;
    Datum_Label: TLabel;
    Rasa: TStringColumn;
    procedure FormCreate(Sender: TObject);
    procedure SpisakCellClick(const Column: TColumn; const ARow: Integer);
    procedure PregledClick(Sender: TObject);
    procedure OdjavaClick(Sender: TObject);
    procedure Datum_EditChange(Sender: TObject);
    procedure SpisakCellDblClick(const Column: TColumn; const Row: Integer);

  private
    { Private declarations }
    procedure OpenPregled(ID: Integer; ImeLjubimca, VrstaLjubimca, RasaLjubimca, ImeVlasnika, PrezimeVlasnika: string; Datum: TDate; Vreme: TTime; Vakcinacija_bol:boolean);
  public
    { Public declarations }
  FSelectedImeLjubimca: string;
  FSelectedVrstaLjubimca: string;
  FSelectedRasa: string;
  FSelectedImeVlasnika: string;
  FSelectedPrezimeVlasnika: string;
  FSelectedDatum: TDate;
  FSelectedVreme: TTime;
  FSelectedID: integer;
  FTerminID: integer;
  FImeLjubimca: string;
  FVrstaLjubimca: string;
  FImeVlasnika: string;
  FPrezimeVlasnika: string;
  FDatum: Tdate;
  FVreme: TTime;
    Vakcinacija_bol: boolean;
  end;

var
  frmTerminiVeterinar: TfrmTerminiVeterinar;

implementation

{$R *.fmx}
uses Main, LogInZaposleni, StariPregled;

function TerminIsInPregled(TerminID: Integer): Boolean;
var
  Query: TFDQuery;
begin
  Query := TFDQuery.Create(nil);
  try
    Query.Connection := GlobalConnection;
    Query.SQL.Text := 'SELECT COUNT(*) FROM pregled WHERE termin = :TerminID';
    Query.ParamByName('TerminID').AsInteger := TerminID;
    Query.Open;
    Result := Query.Fields[0].AsInteger > 0;
  finally
    Query.Free;
  end;
end;

procedure TfrmTerminiVeterinar.Datum_EditChange(Sender: TObject);
var
  Query: TFDQuery;
  Row: Integer;
begin
  Query := TFDQuery.Create(nil);
  try
    Query.Connection := GlobalConnection;
    Query.SQL.Text :=
      'SELECT termin.idtermina,termin.vakcinacija, rasa_ljubimca.rasa, ljubimci.imeljubimca, tipovi_ljubimaca.ime_tipa, vlasnici.imevlasnika, vlasnici.prezimevlasnika, termin.datum, termin.vreme ' +
      'FROM termin ' +
      'JOIN ljubimci ON termin.ljubimac = ljubimci.idljubimca ' +
      'JOIN vlasnici ON ljubimci.vlasnik = vlasnici.idvlasnika ' +
      'JOIN rasa_ljubimca ON ljubimci.rasa = rasa_ljubimca.idrase ' +
      'JOIN tipovi_ljubimaca ON rasa_ljubimca.tipljubimca = tipovi_ljubimaca.idtipa ' +
      'WHERE termin.datum = :Datum ';
    Query.ParamByName('Datum').AsString := FormatDateTime('dd/mm/yyyy', Datum_Edit.Date);
    Query.Open;

    Spisak.RowCount := Query.RecordCount;
    Row := 0;
    while not Query.Eof do
    begin
      Spisak.Cells[0, Row] := Query.FieldByName('idtermina').AsString;
      Spisak.Cells[1, Row] := Query.FieldByName('imeljubimca').AsString;
      Spisak.Cells[2, Row] := Query.FieldByName('ime_tipa').AsString;
      Spisak.Cells[3, Row] := Query.FieldByName('rasa').AsString;
      Spisak.Cells[4, Row] := Query.FieldByName('imevlasnika').AsString;
      Spisak.Cells[5, Row] := Query.FieldByName('prezimevlasnika').AsString;
      Spisak.Cells[6, Row] := Query.FieldByName('datum').AsString;
      Spisak.Cells[7, Row] := Query.FieldByName('vreme').AsString;
      Spisak.Cells[8, Row] := Query.FieldByName('vakcinacija').AsString;
      if TerminIsInPregled(Query.FieldByName('idtermina').AsInteger) then
      Spisak.Cells[9, Row] := 'OBAVLJEN'
    else
      Spisak.Cells[9, Row] := 'NIJE OBAVLJEN';
      Query.Next;
      Inc(Row);
    end;
  finally
    Query.Free;
  end;
end;

procedure TfrmTerminiVeterinar.OpenPregled(ID: Integer; ImeLjubimca, VrstaLjubimca, RasaLjubimca, ImeVlasnika, PrezimeVlasnika: string; Datum: TDate; Vreme: TTime; Vakcinacija_bol:boolean);
var
  frmPregled: TfrmPregled;
begin
  frmPregled := TfrmPregled.Create(nil);
  try
    frmStariPregled.FTerminID := ID;
    frmStariPregled.FImeLjubimca := ImeLjubimca;
    frmStariPregled.FVrstaLjubimca := VrstaLjubimca;
    frmStariPregled.FRasaLjubimca := RasaLjubimca;
    frmStariPregled.FImeVlasnika := ImeVlasnika;
    frmStariPregled.FPrezimeVlasnika := PrezimeVlasnika;
    frmStariPregled.FDatum := Datum;
    frmStariPregled.FVreme := Vreme;
    frmStariPregled.FVakcinacija := Vakcinacija_bol;
    frmStariPregled.Show;
    self.hide;
  finally
    frmPregled.Free;
  end;
end;



procedure TfrmTerminiVeterinar.FormCreate(Sender: TObject);
var
  Query: TFDQuery;
  Row: Integer;
  Datum: string;
begin
  Query := TFDQuery.Create(nil);
  try
    Datum_Edit.Date := Now;
    Datum := FormatDateTime('dd/mm/yyyy', Now);
    Query.Connection := GlobalConnection;
    Query.SQL.Text :=
      'SELECT termin.idtermina, rasa_ljubimca.rasa, termin.vakcinacija, ljubimci.imeljubimca, tipovi_ljubimaca.ime_tipa, vlasnici.imevlasnika, vlasnici.prezimevlasnika, termin.datum, termin.vreme ' +
      'FROM termin ' +
      'JOIN ljubimci ON termin.ljubimac = ljubimci.idljubimca ' +
      'JOIN vlasnici ON ljubimci.vlasnik = vlasnici.idvlasnika ' +
      'JOIN rasa_ljubimca ON ljubimci.rasa = rasa_ljubimca.idrase ' +
      'JOIN tipovi_ljubimaca ON rasa_ljubimca.tipljubimca = tipovi_ljubimaca.idtipa ' +
      'WHERE termin.datum = :Datum';
    Query.ParamByName('Datum').AsString := Datum;
    Query.Open;
    Spisak.RowCount := Query.RecordCount;

    Row := 0;
    while not Query.Eof do
    begin
      Spisak.Cells[0, Row] := Query.FieldByName('idtermina').AsString;
      Spisak.Cells[1, Row] := Query.FieldByName('imeljubimca').AsString;
      Spisak.Cells[2, Row] := Query.FieldByName('ime_tipa').AsString;
      Spisak.Cells[3, Row] := Query.FieldByName('rasa').AsString;
      Spisak.Cells[4, Row] := Query.FieldByName('imevlasnika').AsString;
      Spisak.Cells[5, Row] := Query.FieldByName('prezimevlasnika').AsString;
      Spisak.Cells[6, Row] := Query.FieldByName('datum').AsString;
      Spisak.Cells[7, Row] := Query.FieldByName('vreme').AsString;
      Spisak.Cells[8, Row] := Query.FieldByName('vakcinacija').AsString;
      if TerminIsInPregled(Query.FieldByName('idtermina').AsInteger) then
      Spisak.Cells[9, Row] := 'OBAVLJEN'
    else
      Spisak.Cells[9, Row] := 'NIJE OBAVLJEN';
      Query.Next;
      Inc(Row);
    end;
  finally
    Query.Free;
  end;
end;


procedure TfrmTerminiVeterinar.SpisakCellClick(const Column: TColumn; const ARow: Integer);
begin
      FSelectedID := Spisak.Cells[0, ARow].ToInteger;
  FSelectedImeLjubimca := Spisak.Cells[1, ARow];
  FSelectedVrstaLjubimca := Spisak.Cells[2, ARow];
  FSelectedRasa := Spisak.Cells[3, ARow];
  FSelectedImeVlasnika := Spisak.Cells[4, ARow];
  FSelectedPrezimeVlasnika := Spisak.Cells[5, ARow];
  FSelectedDatum := StrToDate(Spisak.Cells[6, ARow]);
  FSelectedVreme := StrToTime(Spisak.Cells[7, ARow]);
  Vakcinacija_bol := SameText(Spisak.Cells[8, ARow], 'DA');
end;

procedure TfrmTerminiVeterinar.SpisakCellDblClick(const Column: TColumn;
  const Row: Integer);
begin
  if Spisak.Row >= 0 then
  begin
    if Spisak.Cells[9, Spisak.Row] = 'OBAVLJEN' then
    begin
      FSelectedID := Spisak.Cells[0, Spisak.Row].ToInteger;
      FSelectedImeLjubimca := Spisak.Cells[1, Spisak.Row];
      FSelectedVrstaLjubimca := Spisak.Cells[2, Spisak.Row];
      FSelectedRasa := Spisak.Cells[3, Row];
      FSelectedImeVlasnika := Spisak.Cells[4, Row];
      FSelectedPrezimeVlasnika := Spisak.Cells[5, Row];
      FSelectedDatum := StrToDate(Spisak.Cells[6, Row]);
      FSelectedVreme := StrToTime(Spisak.Cells[7, Row]);
      Vakcinacija_bol := SameText(Spisak.Cells[8, Row], 'DA');

      OpenPregled(FSelectedID, FSelectedImeLjubimca, FSelectedVrstaLjubimca, FSelectedRasa,
        FSelectedImeVlasnika, FSelectedPrezimeVlasnika, FSelectedDatum, FSelectedVreme, Vakcinacija_bol);
    end
    else
      ShowMessage('Pregled nije obavljen.');
  end;
end;



procedure TfrmTerminiVeterinar.OdjavaClick(Sender: TObject);
begin
frmLogInZaposleni.show;
self.hide;
end;

procedure TfrmTerminiVeterinar.PregledClick(Sender: TObject);
begin

frmPregled.show;
self.hide;
end;
end.
