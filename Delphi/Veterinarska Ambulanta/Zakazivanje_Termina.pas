unit Zakazivanje_Termina;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs, FMX.ListBox,
  FireDAC.Stan.Intf, FireDAC.Stan.Option, FireDAC.Stan.Param,
  FireDAC.Stan.Error, FireDAC.DatS, FireDAC.Phys.Intf, FireDAC.DApt.Intf,
  FireDAC.Stan.Async, FireDAC.DApt, Data.DB, FireDAC.Comp.DataSet,
  FireDAC.Comp.Client, FMX.StdCtrls, FMX.Controls.Presentation, FMX.Calendar,
  FMX.Objects;

type
  TfrmZakazivanjeTermina = class(TForm)
    Ljubimac: TComboBox;
    Kalendar: TCalendar;
    Nazad: TButton;
    Zakazivanje: TButton;
    Sat: TComboBox;
    Minut: TComboBox;
    Ljubimac_Label: TLabel;
    Minut_Label: TLabel;
    Sat_Label: TLabel;
    Navbar: TImage;
    StyleBook: TStyleBook;
    Forma: TPanel;
    Vakcina: TCheckBox;
    procedure FormCreate(Sender: TObject);
    procedure NazadClick(Sender: TObject);
    procedure ZakazivanjeClick(Sender: TObject);
    procedure KalendarChange(Sender: TObject);
    procedure FormActivate(Sender: TObject);
  private
    SelectedDate: string;
    IDLjubimca: Integer;
    function GetIDLjubimca(const imeLjubimca: string): Integer;
  public
    { Public declarations }
  end;

var
  frmZakazivanjeTermina: TfrmZakazivanjeTermina;

implementation

{$R *.fmx}
uses Main, LoginForma, Vlasnik_GlavnaForma,SpisakTermina;

procedure TfrmZakazivanjeTermina.FormActivate(Sender: TObject);
begin
var
  MyQuery: TFDQuery;

begin
  MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Clear;
    MyQuery.SQL.Text := 'SELECT * FROM LJUBIMCI WHERE VLASNIK = :IDVlasnika';
    MyQuery.ParamByName('IDVlasnika').AsInteger := frmLogIn.UlogovaniVlasnikID;
    MyQuery.Open;

    Ljubimac.Clear;

    while not MyQuery.Eof do
    begin
      Ljubimac.Items.Add(MyQuery.FieldbyName('ImeLjubimca').AsString);
      MyQuery.Next;
    end;
  finally
    MyQuery.Free;
    MyQuery.Close;
  end;


end;
end;

procedure TfrmZakazivanjeTermina.FormCreate(Sender: TObject);
var
h: integer;
  m: integer;
begin
   for h := 0 to 23 do
      sat.Items.Add(Format('%2.2d', [h]));
  for m := 0 to 59 do
      minut.Items.Add(Format('%2.2d', [m]));
end;

procedure TfrmZakazivanjeTermina.KalendarChange(Sender: TObject);
begin
  SelectedDate := FormatDateTime('dd/mm/yyyy', Kalendar.Date);
end;

procedure TfrmZakazivanjeTermina.NazadClick(Sender: TObject);
begin
frmspisaktermina.show;
self.Hide;
end;

procedure TfrmZakazivanjeTermina.ZakazivanjeClick(Sender: TObject);
var
  SelectedTime: TDateTime;
  MyQuery: TFDQuery;
begin
IDLjubimca:= GetIDLjubimca(Ljubimac.Selected.text);
  SelectedTime := EncodeTime(StrToInt(Sat.Selected.Text), StrToInt(Minut.Selected.Text), 0, 0);

MyQuery := TFDQuery.Create(nil);
try
  MyQuery.Connection := GlobalConnection;
  MyQuery.SQL.Text := 'SELECT * FROM Termin WHERE Datum = :Datum AND Vreme = :Vreme';
  MyQuery.ParamByName('Datum').AsString := SelectedDate;
  MyQuery.ParamByName('Vreme').AsString := FormatDateTime('hh:nn', SelectedTime);
  MyQuery.Open;
  if not MyQuery.IsEmpty then
  begin
    ShowMessage('Termin već postoji u izabranom datumu i vremenu.');
    Exit;
  end;
finally
  MyQuery.Free;
end;

  MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    if Vakcina.IsChecked then
begin
  MyQuery.SQL.Text := 'INSERT INTO Termin (Klijent, Ljubimac, Datum, Vreme, Vakcinacija) VALUES (:Klijent, :Ljubimac, :Datum, :Vreme, :Vakcinacija)';
  MyQuery.ParamByName('Vakcinacija').AsString := 'DA';
end
else
begin
  MyQuery.SQL.Text := 'INSERT INTO Termin (Klijent, Ljubimac, Datum, Vreme) VALUES (:Klijent, :Ljubimac, :Datum, :Vreme)';
end;
  MyQuery.ParamByName('Klijent').AsInteger := frmlogin.UlogovaniVlasnikID;
    MyQuery.ParamByName('Ljubimac').AsInteger := IDLjubimca;
    MyQuery.ParamByName('Datum').AsString := SelectedDate;
    MyQuery.ParamByName('Vreme').AsString := FormatDateTime('hh:nn', SelectedTime);
    MyQuery.ExecSQL;

  finally
    MyQuery.Free;
    ShowMessage('Termin je uspešno sačuvan.');
    frmspisaktermina.show;
    self.hide;
  end;
end;
function TfrmZakazivanjeTermina.GetIDLjubimca(const imeLjubimca: string): Integer;
var
  MyQuery: TFDQuery;
begin
  Result := -1;
  MyQuery := TFDQuery.Create(nil);
  try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'SELECT idljubimca FROM ljubimci WHERE imeljubimca = :ime';
    MyQuery.ParamByName('ime').AsString := imeLjubimca;
    MyQuery.Open;

    if not MyQuery.IsEmpty then
      Result := MyQuery.FieldByName('idljubimca').AsInteger;
  finally
    MyQuery.Free;
  end;
end;

end.
