unit Pitanje5;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs, FMX.Memo.Types,
  FMX.StdCtrls, FMX.Controls.Presentation, FMX.ScrollBox, FMX.Memo,
  FireDAC.Stan.Intf, FireDAC.Stan.Option, FireDAC.Stan.Param,
  FireDAC.Stan.Error, FireDAC.DatS, FireDAC.Phys.Intf, FireDAC.DApt.Intf,
  FireDAC.Stan.Async, FireDAC.DApt, Data.DB, FireDAC.Comp.DataSet,
  FireDAC.Comp.Client;

type
  TfrmPitanje5 = class(TForm)
    Memo: TMemo;
    Da: TRadioButton;
    Ne: TRadioButton;
    Dalje: TButton;
    Nazad: TButton;
    StyleBook: TStyleBook;
    procedure NazadClick(Sender: TObject);
    procedure FormActivate(Sender: TObject);
    procedure FormClose(Sender: TObject; var Action: TCloseAction);
    procedure DaljeClick(Sender: TObject);
    function ProveraNizaFikcijaNe(const OriginalniNiz: TArray<Integer>): TArray<Integer>;
    function ProveraNizaFikcijaDa(const OriginalniNiz: TArray<Integer>): TArray<Integer>;
  private
    { Private declarations }
  public
    { Public declarations }
    NizPitanje5: TArray<Integer>;
  end;

var
  frmPitanje5: TfrmPitanje5;

implementation

{$R *.fmx}
uses Main, Pitanje4, Pitanje6;

procedure TfrmPitanje5.DaljeClick(Sender: TObject);
var
Poruka: String;
i: integer;
begin
if Da.IsChecked then
begin
NizPitanje5 := ProveraNizaFikcijaDa(frmPitanje4.NizPitanje4);
if Length(NizPitanje5) = 0 then
NizPitanje5 := frmPitanje4.NizPitanje4;
//Pitanje4ID:= 1;
frmPitanje6.Show;
self.Hide;
end;
if Ne.IsChecked then
begin
NizPitanje5 := ProveraNizaFikcijaNe(frmPitanje4.NizPitanje4);
if Length(NizPitanje5) = 0 then
NizPitanje5 := frmPitanje4.NizPitanje4;
//Pitanje4ID:= 0;
if Length(NizPitanje5) > 0 then
  begin
    Poruka := '';

    for i := Low(NizPitanje5) to High(NizPitanje5) do
    begin
      Poruka := Poruka + IntToStr(NizPitanje5[i]) + ', ';
    end;
    SetLength(Poruka, Length(Poruka) - 2);
    ShowMessage('Vrednosti u nizu su: ' + Poruka);
  end;
frmPitanje6.Show;
self.Hide;
end;
end;

procedure TfrmPitanje5.FormActivate(Sender: TObject);
begin
Left := Round((Screen.Width - Width)/2);
Top := Round ((Screen.Height - Height)/2);
end;

procedure TfrmPitanje5.FormClose(Sender: TObject; var Action: TCloseAction);
begin
Application.Destroy;
end;

procedure TfrmPitanje5.NazadClick(Sender: TObject);
begin
frmPitanje4.Show;
self.Hide;
end;

function TfrmPitanje5.ProveraNizaFikcijaDa(const OriginalniNiz: TArray<Integer>): TArray<Integer>;
var
  MyQuery: TFDQuery;
  ResultArray: TArray<Integer>;
  i: Integer;
  Found: Boolean;
  KnjigaID: integer;
begin
  SetLength(ResultArray, 0);

  MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'SELECT IDKnjige FROM Knjige WHERE Fikcija = ''1''';
    MyQuery.Open;

    ShowMessage('Broj elemenata u OriginalniNiz: ' + Length(OriginalniNiz).ToString);
    ShowMessage('Broj Clanova kverija' + (MyQuery.RecordCount).ToString);

    while not MyQuery.Eof do
    begin
      KnjigaID := MyQuery.FieldByName('IDKnjige').AsInteger;
//      showMessage(KnjigaID.ToString);

      Found := False;
      for i := Low(OriginalniNiz) to High(OriginalniNiz) do
      begin
//        ShowMessage('Proveravam ' + KnjigaID.ToString + ' sa ' + OriginalniNiz[i].ToString);

        if KnjigaID = OriginalniNiz[i] then
        begin
//          ShowMessage('Pronađena knjiga');
          Found := True;
          Break;
        end;
      end;

      if Found then
      begin
        SetLength(ResultArray, Length(ResultArray) + 1);
        ResultArray[Length(ResultArray) - 1] := KnjigaID;
      end;

      MyQuery.Next;
    end;

  finally
    MyQuery.Free;
  end;

  Result := ResultArray;
//  ShowMessage('Broj elemenata u rezultatskom nizu: ' + Length(ResultArray).ToString);
end;

function TfrmPitanje5.ProveraNizaFikcijaNe(const OriginalniNiz: TArray<Integer>): TArray<Integer>;
var
  MyQuery: TFDQuery;
  ResultArray: TArray<Integer>;
  i: Integer;
  Found: Boolean;
  KnjigaID: integer;
begin
  SetLength(ResultArray, 0);

  MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'SELECT IDKnjige FROM Knjige WHERE Fikcija = ''0''';
    MyQuery.Open;

    ShowMessage('Broj elemenata u OriginalniNiz: ' + Length(OriginalniNiz).ToString);
    ShowMessage('Broj Clanova kverija' + (MyQuery.RecordCount).ToString);

    while not MyQuery.Eof do
    begin
      KnjigaID := MyQuery.FieldByName('IDKnjige').AsInteger;
//      showMessage(KnjigaID.ToString);

      Found := False;
      for i := Low(OriginalniNiz) to High(OriginalniNiz) do
      begin
//        ShowMessage('Proveravam ' + KnjigaID.ToString + ' sa ' + OriginalniNiz[i].ToString);

        if KnjigaID = OriginalniNiz[i] then
        begin
//          ShowMessage('Pronađena knjiga');
          Found := True;
          Break;
        end;
      end;

      if Found then
      begin
        SetLength(ResultArray, Length(ResultArray) + 1);
        ResultArray[Length(ResultArray) - 1] := KnjigaID;
      end;

      MyQuery.Next;
    end;

  finally
    MyQuery.Free;
  end;

  Result := ResultArray;
//  ShowMessage('Broj elemenata u rezultatskom nizu: ' + Length(ResultArray).ToString);
end;

end.
