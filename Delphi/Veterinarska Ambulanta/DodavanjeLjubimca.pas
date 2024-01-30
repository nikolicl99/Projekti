
unit DodavanjeLjubimca;

interface

uses
  System.SysUtils, System.Types, System.UITypes, System.Classes, System.Variants,
  FMX.Types, FMX.Controls, FMX.Forms, FMX.Graphics, FMX.Dialogs, FMX.StdCtrls,
  FMX.Controls.Presentation, FMX.Edit, FMX.ListBox, FireDAC.Stan.Intf,
  FireDAC.Stan.Option, FireDAC.Stan.Param, FireDAC.Stan.Error, FireDAC.DatS,
  FireDAC.Phys.Intf, FireDAC.DApt.Intf, FireDAC.Stan.Async, FireDAC.DApt,
  Data.DB, FireDAC.Comp.DataSet, FireDAC.Comp.Client, FMX.Objects;

type
  TfrmDodavanjeLjubimca = class(TForm)
    Ime: TEdit;
    Starost: TEdit;
    Ime_Label: TLabel;
    Starost_Label: TLabel;
    Unos: TButton;
    Nazad: TButton;
    Vrsta_Label: TLabel;
    Muski: TRadioButton;
    Zenski: TRadioButton;
    Pol_Label: TLabel;
    Vrsta: TComboBox;
    Navbar: TImage;
    StyleBook: TStyleBook;
    Forma: TPanel;
    Rasa: TComboBox;
    Rasa_Label: TLabel;
    procedure NazadClick(Sender: TObject);
    procedure FormCreate(Sender: TObject);
    procedure UnosClick(Sender: TObject);
    procedure Vrstabox;
    procedure VrstaSelect(Sender: TObject);
    procedure RasaSelect(Sender: TObject);
  private
    { Private declarations }
    SelectedRasaID: integer;
  public
    { Public declarations }
  end;

var
  frmDodavanjeLjubimca: TfrmDodavanjeLjubimca;

implementation

{$R *.fmx}
uses Vlasnik_GlavnaForma, Main, LoginForma, SpisakZivotinja;

procedure TfrmDodavanjeLjubimca.VrstaBox;
var
MyQuery: TFDQuery;
begin
MyQuery := TFDQuery.Create(nil);
try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'SELECT * FROM TIPOVI_LJUBIMACA';
    MyQuery.Open;
    vrsta.Clear;
    while not MyQuery.Eof do
begin
Vrsta.Items.AddObject(MyQuery.FieldByName('Ime_Tipa').AsString, TObject(MyQuery.FieldByName('IDTipa').AsInteger));
   MyQuery.next;
 end;
  finally
        MyQuery.Free;
end;
end;

procedure TfrmDodavanjeLjubimca.FormCreate(Sender: TObject);
begin
  Vrstabox;
  if Vrsta.Items.Count > 0 then
  begin
    Vrsta.ItemIndex := 0;
    VrstaSelect(Vrsta);
  end;

end;

procedure TfrmDodavanjeLjubimca.VrstaSelect(Sender: TObject);
var
  MyQuery: TFDQuery;
begin
  if Vrsta.ItemIndex <> -1 then
  begin
    MyQuery := TFDQuery.Create(nil);
    try
      MyQuery.Connection := GlobalConnection;
      Rasa.Items.Clear;
      MyQuery.SQL.Text := 'SELECT * FROM Rasa_Ljubimca WHERE tipljubimca = :Tip';
      MyQuery.ParamByName('Tip').AsInteger := Integer(Vrsta.Items.Objects[Vrsta.ItemIndex]);
      MyQuery.Open;
      while not MyQuery.Eof do
      begin
        Rasa.Items.AddObject(MyQuery.FieldByName('Rasa').AsString, TObject(MyQuery.FieldByName('IDRase').AsInteger));
        MyQuery.Next;
      end;
      MyQuery.Close;
    finally
      MyQuery.Free;
    end;
  end;
end;

procedure TfrmDodavanjeLjubimca.RasaSelect(Sender: TObject);
begin
  if Rasa.ItemIndex <> -1 then
  begin
    SelectedRasaID := Integer(Rasa.Items.Objects[Rasa.ItemIndex]);
  end;
end;

procedure TfrmDodavanjeLjubimca.NazadClick(Sender: TObject);
begin
frmGlavniMeni.show;
self.hide;
end;

procedure TfrmDodavanjeLjubimca.UnosClick(Sender: TObject);
var
MyQuery: TFDQuery;
begin
MyQuery := TFDQuery.Create(nil);
try
    MyQuery.Connection := GlobalConnection;
    MyQuery.SQL.Text := 'INSERT INTO LJUBIMCI (IMELJUBIMCA, STAROST, VLASNIK, POL, RASA) VALUES (:ime, :starost, :vlasnik, :pol, :rasa)';
    MyQuery.ParamByName('ime').AsString := Ime.Text;
    MyQuery.ParamByName('starost').AsString := Starost.text;
    MyQuery.ParamByName('vlasnik').AsInteger := frmlogin.UlogovaniVlasnikID;
    MyQuery.ParamByName('rasa').AsInteger := SelectedRasaID;
    if Muski.IsChecked then
     MyQuery.ParamByName('pol').AsString := 'Muski'
     else if Zenski.IsChecked then
     MyQuery.ParamByName('pol').AsString := 'Zenski'
     else if (Muski.IsChecked) and (Zenski.IsChecked) then
     ShowMessage('Nije moguce izabrati oba pola')
     else
     ShowMessage('Nijedan pol nije izabran');

     MyQuery.ExecSQL;

    finally
    MyQuery.Free;
    ShowMessage('Ljubimac dodat');
    frmspisakzivotinja.show;
    self.hide;
end;
end;
end.
